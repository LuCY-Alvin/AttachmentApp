package com.mildp.familyatschool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mildp.familyatschool.ui.components.IntroductionScreen
import com.mildp.familyatschool.ui.components.SurveyScreen
import com.mildp.familyatschool.ui.theme.FamilyAtSchoolTheme

class SurveyActivity : ComponentActivity() {

    companion object {
        private const val TAG = "SurveyActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyAtSchoolTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "Introduction") {
                    composable("Introduction"){
                        IntroductionScreen(navController)
                    }
                    composable("Survey") {
                        SurveyScreen()
                    }
                }
            }
        }
    }
}
