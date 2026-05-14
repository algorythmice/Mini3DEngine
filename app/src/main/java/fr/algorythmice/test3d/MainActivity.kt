package fr.algorythmice.test3d

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar

import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: SceneView
    private lateinit var loadModel: LoadModel
    private lateinit var loadingSpinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById(R.id.sceneView)
        loadingSpinner = findViewById(R.id.loadingSpinner)

        loadModel = LoadModel(this, sceneView) {
            loadingSpinner.visibility = View.GONE
            sceneView.visibility = View.VISIBLE
        }

        loadModel.addModel("models/airportbase.glb", Vector3(11.16f, 0.0f, 10.34f), yaw = 180.0f, scale = 0.047f)
        loadModel.addModel("models/intersection.glb", Vector3(4.5f, 0.0f, 10.5f), yaw = 90.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(4.5f, 0.0f, 11.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/airportextension.glb", Vector3(11.16f, 0.0f, 7.05f), yaw = 180.0f, scale = 0.047f)
        loadModel.addModel("models/airportroad.glb", Vector3(10.98f, 0.0f, 0.35f), yaw = 180.0f, scale = 0.02f)
        loadModel.addModel("models/airportcurvedroad.glb", Vector3(10.55f, 0.0f, 4.28f), yaw = 180.0f, scale = 0.02f)
        loadModel.addModel("models/tourdecontrole.glb", Vector3(9.31f, 0.0f, 3.07f), yaw = 180.0f, scale = 0.047f)
        loadModel.addModel("models/airportshed.glb", Vector3(7.64f, 0.0f, 1.57f), yaw = 270.0f, scale = 0.047f)
        loadModel.addModel("models/entrepot.glb", Vector3(-5.69f, 0.0f, 6.61f), yaw = 270.0f, scale = 1.5f)
        loadModel.addModel("models/roadend.glb", Vector3(-4.5f, 0.0f, 9.5f), yaw = 90.0f, scale = 1.0f)
        loadModel.addModel("models/road-bend.glb", Vector3(8.5f, 0.0f, 10.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/parcking.glb", Vector3(7.0f, 0.005f, 8.0f), yaw = 90.0f, scale = 1f)
        loadModel.addModel("models/tes.glb", Vector3(5.5f, 0.0f, 10.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(6.5f, 0.0f, 10.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(7.5f, 0.0f, 10.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(4.5f, 0.0f, 9.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(4.5f, 0.0f, 8.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(4.5f, 0.0f, 7.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/airportextension.glb", Vector3(9.76f, 0.0f, 7.05f), yaw = 180.0f, scale = 0.047f)
        loadModel.addModel("models/airportroad.glb", Vector3(10.98f, 0.0f, -5.04f), yaw = 180.0f, scale = 0.02f)
        loadModel.addModel("models/tes.glb", Vector3(4.5f, 0.0f, 6.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(4.5f, 0.0f, 5.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/intersection.glb", Vector3(4.5f, 0.0f, 4.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(3.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(2.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(0.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(1.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-2.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-3.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-1.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-0.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-5.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-6.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-8.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-7.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-9.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-11.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-10.5f, 0.0f, 4.5f), yaw = 360.0f, scale = 1.0f)
        loadModel.addModel("models/intersection.glb", Vector3(-4.5f, 0.0f, 4.5f), yaw = 0.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-4.5f, 0.0f, 5.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-4.5f, 0.0f, 6.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-4.5f, 0.0f, 8.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/tes.glb", Vector3(-4.5f, 0.0f, 7.5f), yaw = 270.0f, scale = 1.0f)
        loadModel.addModel("models/entrepot.glb", Vector3(-3.22f, 0.0f, 6.61f), yaw = 90.0f, scale = 1.5f)
        loadModel.addModel("models/airportcurvedroad.glb", Vector3(8.07f, 0.0f, 4.28f), yaw = 90.0f, scale = 0.02f)





    }

    override fun onResume() { super.onResume(); sceneView.resume() }
    override fun onPause()  { sceneView.pause(); super.onPause() }
    override fun onDestroy(){ sceneView.destroy(); super.onDestroy() }
}
