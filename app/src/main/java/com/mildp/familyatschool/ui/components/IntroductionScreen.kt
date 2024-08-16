package com.mildp.familyatschool.ui.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mildp.familyatschool.App
import com.mildp.familyatschool.viewmodels.IntroViewModel

@Composable
fun IntroductionScreen(
    navController: NavHostController
) {
    val introViewModel: IntroViewModel = viewModel()

    BackHandler(enabled = true) {
        Toast.makeText(App.instance(),"請繼續作答，不要退回前一頁，感謝。", Toast.LENGTH_SHORT).show()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = introViewModel.spotName,
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Text(
            text = introViewModel.spotIntro(),
            modifier = Modifier.padding(5.dp),
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            onClick = { introViewModel.onStart(navController) }
        ){

        }
    }
}