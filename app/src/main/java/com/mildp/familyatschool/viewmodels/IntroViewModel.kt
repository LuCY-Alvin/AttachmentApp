package com.mildp.familyatschool.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.mildp.familyatschool.Constants.mmkv

class IntroViewModel : ViewModel() {

    var spotName by mutableStateOf(mmkv.decodeString("SpotName","").toString())

    fun spotIntro(): String {
        when(spotName) {
            "景點一" -> return "歡迎到達地點一！現在你們面前的是醉月湖，以前叫做牛湳池（「湳」字意指泥沼地），是台大的十二景之一。醉月湖有許多動、植物，看看周遭，你能看見小鴨、小鵝嗎？"
            "景點二" -> return "歡迎到達地點二！從這兒你可以清楚看見醉月湖中央的亭，仔細看，沒有橋可以通往那座涼亭，是不是很特別呢？岸邊有好多棵垂柳，十分詩情畫意！"
            "景點三" -> return "歡迎到達地點三！從這個橋往旁邊的小湖中看，會看見美麗的荷花。天氣暖和時，粉紅色的荷花十分美麗，還看得見可愛的小蜻蜓呢！就像古典文學所說的「小荷才露尖尖角，早有蜻蜓立上頭。」"
        }
        return ""
    }

    fun onStart(navController: NavController){
        navController.navigate("Survey")
    }
}