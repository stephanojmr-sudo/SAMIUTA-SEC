package com.kitutu.matokeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kitutu.matokeo.ui.AppViewModel
import com.kitutu.matokeo.ui.NavGraph
import com.kitutu.matokeo.ui.theme.MatokeoKitutuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MatokeoKitutuTheme {
                val viewModel: AppViewModel = viewModel(factory = AppViewModel.factory(application))
                NavGraph(viewModel)
            }
        }
    }
}
