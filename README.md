# Parent-Child Attachment App

親子依附應用程式是以 Kotlin 編寫的 Android 應用程式，透過控制藍芽掃描以估算對應手機與手環的物理距離，依據 GPS 制定 Location-based 的 EMA，依此探討親子互動距離與依附關係。

## 聲明

- 本應用程式僅用於研究目的，僅供研究參與人員使用。
- The application is for research purposes only and is intended solely for use by research participants.

## 功能
- 控制手機藍芽掃描與GPS，估算相對裝置的物理距離
- 針對手機定位做出特定調查

## 安裝
### 設定
- Android Studio 版本: Android Studio Electric Eel | 2022.1.1
- Gradle 版本: 7.5
- 適用的 Android 最低版本：API 26 (Android 8.0 Oreo)
  
### 步驟
- 透過 git clone https://github.com/LuCY-Alvin/AttachmentApp 
- 在 Android Studio 中開啟，並同步安裝需要的 dependencies 和 plugins
  
### 使用方法
- 設定配對的手機與手環
- 開啟本應用程式所需的各項權限後開啟實驗，即開始進行資料蒐集
- 抵達指定地點後即會跳出心情調查

## 技術
- Kotlin
- Android SDK
- Jetpack Compose
- GPS Location
- Room Database
- LiveData and ViewModel
- Kotlin Coroutines
- OkHttp3

## APK
- 下載連結: https://mega.nz/file/ZeU1XIoZ#88OtcZcQkZwy8zNN7Ww5OKbeA2NlR5JCJ-6y5OpIDoY
