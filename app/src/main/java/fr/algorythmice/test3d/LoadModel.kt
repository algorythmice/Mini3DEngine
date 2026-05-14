package fr.algorythmice.test3d

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.Texture
import java.util.ArrayDeque
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri

class LoadModel(
    private val context: Context,
    private val sceneView: SceneView,
    private val onAllLoaded: (() -> Unit)? = null
) {
    private val yawDeg = 45f
    private val pitchDeg = 35.264
    private var targetX = 0f
    private var targetZ = 0f
    private var distance = 8f

    private val PAN_SPEED = 0.006f
    private val ZOOM_SPEED = 0.01f
    private val MIN_ZOOM = 6f
    private val MAX_ZOOM = 17f
    private val MOVE_LIMIT = 12f
    private val MIN_CAM_HEIGHT = 0.6f

    private var prevX = 0f
    private var prevY = 0f
    private var isPinching = false
    private var prevPinchDist = 0f

    private var touchStartX = 0f
    private var touchStartY = 0f
    private var movedTooMuch = false
    private val TAP_SLOP = 10 // px

    // --- Plateau ---
    private val BOARD_WIDTH = 24f   // largeur X
    private val BOARD_HEIGHT = 24f  // profondeur Z



    private data class LoadReq(
        val modelPath: String,
        val position: Vector3,
        val yaw: Float,
        val scale: Float,
        val onClick: (() -> Unit)? = null
    )

    private val queue = ArrayDeque<LoadReq>()
    private var isLoading = false
    private var sceneReady = false

    init {
        sceneView.setBackgroundColor("#87CEEB".toColorInt())

        val listener = object : com.google.ar.sceneform.Scene.OnUpdateListener {
            override fun onUpdate(frameTime: com.google.ar.sceneform.FrameTime?) {
                if (!sceneReady) {
                    sceneReady = true
                    sceneView.scene.removeOnUpdateListener(this)
                    addBoardPlane(BOARD_WIDTH, BOARD_HEIGHT)
                    updateCamera()
                    setupTouchControls()
                    drainQueue()
                }
            }
        }
        sceneView.scene.addOnUpdateListener(listener)
    }


    fun addModel(
        modelPath: String,
        position: Vector3,
        yaw: Float = 0f,
        scale: Float = 1f,
        onClick: (() -> Unit)? = null
    ) {
        queue.addLast(LoadReq(modelPath, position, yaw, scale, onClick))
        drainQueue()
    }


    // Charge un modèle à la fois pour éviter les crashs natifs
    private fun drainQueue() {
        if (!sceneReady) return
        if (isLoading) return
        val req = queue.pollFirst() ?: run {
            // Si plus rien à charger → on prévient
            if (!isLoading) {
                onAllLoaded?.invoke()
            }
            return
        }
        isLoading = true

        try {
            // registryId unique pour ne pas réutiliser un cache invalide
            val regId = "${req.modelPath}@${System.nanoTime()}"

            ModelRenderable.builder()
                .setSource(
                    context,
                    RenderableSource.builder()
                        .setSource(
                            context,
                            req.modelPath.toUri(),
                            RenderableSource.SourceType.GLB
                        )
                        .setScale(req.scale)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build()
                )
                .setRegistryId(regId)
                .build()
                .thenAccept { renderable ->
                    // On attache le modèle
                    Node().apply {
                        this.renderable = renderable
                        setParent(sceneView.scene)
                        localPosition = req.position
                        localRotation = Quaternion.axisAngle(Vector3.up(), req.yaw)
                        if (req.onClick != null) {
                            setOnTapListener { _: HitTestResult, motionEvent: MotionEvent ->
                                if (!movedTooMuch && motionEvent.action == MotionEvent.ACTION_UP) {
                                    req.onClick.invoke()
                                }
                            }
                        }

                    }
                    isLoading = false
                    drainQueue()
                }
                .exceptionally {
                    Log.e("LoadModel", "Erreur de chargement: ${req.modelPath}", it)
                    isLoading = false
                    drainQueue()
                    null
                }
        } catch (e: Exception) {
            Log.e("LoadModel", "Exception synchro lors du chargement: ${req.modelPath}", e)
            isLoading = false
            drainQueue()
        }
    }

    // --- Gestion tactile ---
    private fun setupTouchControls() {
        sceneView.scene.addOnPeekTouchListener { hitTestResult: HitTestResult, e: MotionEvent ->
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    prevX = e.x
                    prevY = e.y
                    touchStartX = e.x
                    touchStartY = e.y
                    movedTooMuch = false
                    isPinching = false
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    if (e.pointerCount == 2) {
                        prevPinchDist = pinchDistance(e)
                        isPinching = true
                        movedTooMuch = true // si on commence un pinch → pas un clic
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isPinching && e.pointerCount >= 2) {
                        // --- ZOOM ---
                        val dNow = pinchDistance(e)
                        val delta = (dNow - prevPinchDist) * ZOOM_SPEED
                        prevPinchDist = dNow
                        distance = (distance - delta).coerceIn(MIN_ZOOM, MAX_ZOOM)
                        updateCamera()
                    } else if (!isPinching && e.pointerCount == 1) {
                        // --- Vérifie si on a assez bougé pour annuler un clic ---
                        val moveDist = kotlin.math.hypot(
                            (e.x - touchStartX).toDouble(),
                            (e.y - touchStartY).toDouble()
                        )
                        if (moveDist > TAP_SLOP) {
                            movedTooMuch = true
                        }

                        // --- PAN ---
                        val dx = (e.x - prevX) * PAN_SPEED
                        val dy = (e.y - prevY) * PAN_SPEED
                        prevX = e.x
                        prevY = e.y

                        val r = rightXZ()
                        val f = forwardXZ()

                        var nx = targetX - (r.x * dx + f.x * (-dy))
                        var nz = targetZ - (r.z * dx + f.z * (-dy))

                        nx = nx.coerceIn(-MOVE_LIMIT, MOVE_LIMIT)
                        nz = nz.coerceIn(-MOVE_LIMIT, MOVE_LIMIT)

                        targetX = nx
                        targetZ = nz
                        updateCamera()
                    }
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    if (e.pointerCount == 2 && isPinching) {
                        val remainingIndex = if (e.actionIndex == 0) 1 else 0
                        prevX = e.getX(remainingIndex)
                        prevY = e.getY(remainingIndex)
                        isPinching = false
                    }
                }
                MotionEvent.ACTION_UP -> {
                    isPinching = false
                }
            }
        }
    }

    // --- Mouvements caméra ---
    private fun updateCamera() {
        val cam = sceneView.scene.camera
        val back = backFixed()

        var cx = targetX + back.x * distance
        var cy = (0f + back.y * distance).coerceAtLeast(MIN_CAM_HEIGHT)
        var cz = targetZ + back.z * distance

        var adjustPivot = false

        if (cx < -MOVE_LIMIT) { cx = -MOVE_LIMIT; adjustPivot = true }
        if (cx >  MOVE_LIMIT) { cx =  MOVE_LIMIT; adjustPivot = true }
        if (cz < -MOVE_LIMIT) { cz = -MOVE_LIMIT; adjustPivot = true }
        if (cz >  MOVE_LIMIT) { cz =  MOVE_LIMIT; adjustPivot = true }

        if (adjustPivot) {
            targetX = cx - back.x * distance
            targetZ = cz - back.z * distance
        }

        cam.worldPosition = Vector3(cx, cy, cz)
        val lookAt = Vector3(targetX, 0f, targetZ)
        val dir = Vector3.subtract(lookAt, cam.worldPosition).normalized()
        cam.worldRotation = Quaternion.lookRotation(dir, Vector3.up())
    }

    // --- Vecteurs utilitaires ---
    private fun forwardFixed(): Vector3 {
        val yaw = Math.toRadians(yawDeg.toDouble())
        val pitch = Math.toRadians(pitchDeg)
        val fx = sin(yaw) * kotlin.math.cos(pitch)
        val fy = -sin(pitch)
        val fz = kotlin.math.cos(yaw) * kotlin.math.cos(pitch)
        return Vector3(fx.toFloat(), fy.toFloat(), fz.toFloat()).normalized()
    }
    private fun backFixed(): Vector3 = forwardFixed().scaled(-1f)
    private fun rightXZ(): Vector3 {
        val f = forwardFixed()
        return Vector3(-f.z, 0f, f.x).normalized()
    }
    private fun forwardXZ(): Vector3 {
        val f = forwardFixed()
        val v = Vector3(f.x, 0f, f.z)
        val len = v.length()
        return if (len > 0f) Vector3(v.x / len, 0f, v.z / len) else Vector3(0f, 0f, -1f)
    }
    private fun pinchDistance(e: MotionEvent): Float {
        if (e.pointerCount < 2) return 0f
        val dx = e.getX(0) - e.getX(1)
        val dy = e.getY(0) - e.getY(1)
        return sqrt(dx * dx + dy * dy)
    }

    // --- Grille ---
    private fun addBoardPlane(width: Float, height: Float) {
        Texture.builder()
            .setSource(context, R.drawable.board)
            .build()
            .thenCompose { texture ->
                MaterialFactory.makeOpaqueWithTexture(context, texture)
            }
            .thenAccept { mat ->
                val plane = ShapeFactory.makeCube(
                    Vector3(width, 0.01f, height),
                    Vector3(0f, 0f, 0f),
                    mat
                )
                Node().apply {
                    renderable = plane
                    setParent(sceneView.scene)

                    localRotation = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 180f)
                }
            }
    }



}
