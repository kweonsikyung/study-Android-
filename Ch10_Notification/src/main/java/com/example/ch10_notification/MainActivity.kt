package com.example.ch10_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import com.example.ch10_notification.databinding.ActivityMainBinding
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //퍼미션 허용 요청 확인
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted ->
            if(isGranted){
                Log.d("kweon", "callback, granted..")
            } else{
                Log.d("kweon","callback, denied..")
            }
        }
        requestPermissionLauncher.launch("android.permission.ACCESS_FINE_LOCATION")
        requestPermissionLauncher.launch("android.permission.ACCESS_COARSE_LOCATION")
        //퍼미션 허용 확인
        val status1 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")
        val status2 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION")
        if(status1==PackageManager.PERMISSION_GRANTED && status2 == PackageManager.PERMISSION_GRANTED ){
            Log.d("kweon","permission granted")
        }else{
            Log.d("kweon", "permission denied")
        }
        //이벤트 연결
        binding.notificationButton.setOnClickListener{
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val builder: Builder
            //26버전 이하
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                //26버전 이상 -> 부터 채널이라는 개념 생김
                //한 어플에서도 알람을 구분 가능해서 사용자가 선택적으로 알람 받게 끔
                val channelId="one-channel"
                val channelName="My Channel One"
                val channel = NotificationChannel(
                    channelId, // 첫번째 매개변수 : 채널의 식별값
                    channelName, // 두번째 매개변수 : 설정화면에 표시할 채널 이름
                    NotificationManager.IMPORTANCE_DEFAULT //알림 중요도
                ).apply {
                    description = "My Channel One Description"
                    setShowBadge(true)
                    //링톤 객체 얻기
                    val uri: Uri = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                    //알림음 재생
                    setSound(uri, audioAttributes)
                    //진동 울릴 지 여부
                    enableVibration(true)

                }
                //채널을 NotificationManager에 등록
                manager.createNotificationChannel(channel)
                //채널 이용하여 빌더 생성
                builder= Builder(this, channelId)
            } else{
                //26버전 이하
                builder = NotificationCompat.Builder(this)
            }
            builder.run {
                //알림의 기본 정보
                setSmallIcon(R.drawable.small)
                setWhen(System.currentTimeMillis())
                setContentTitle("권시경")
                setContentText("답장주세요.")
                setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.big))
            }
            val KEY_TEXT_REPLY = "key_text_reply"
            var replyLabel:String = "답장"
            //원격입력
            //RemoteInput에 사용자 입력을 받는 정보를 설정한 후 액션에 추가
            var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
                setLabel(replyLabel)
                build()
            }

            //인텐트 이용
            //13장에서 자세히 배우고 간단하게는 '앱의 컴포넌트를 실행하는 데 필요한 정보' 정도로 생각
            val replyIntent = Intent(this, ReplyReceiver::class.java)

            //인텐트가 있어야 알림에서 원하는 컴포넌트 실행할 수 있음
            //그런데 인텐트는 앱의 코드에서 준비
            //이 인텐트로 실제 컴포넌트를 실행하는 시점은 앱에서 정할 수 없음
            //따라서 인텐트를 준비한 후 Notification 객체에 담아서 이벤트가 발생할 때 인텐트를 실행해 달라고
            //시스템에 의뢰해야함
            //이 의뢰를 PendingIntent 클래스 이용
            //PendingIntent 클래스는 컴포넌트별로 실행을 의뢰하는 함수를 제공
            //3번째 매개변수에 인텐트 정보 등록
            //4번째 매개변수에는 똑같은 알림 발생했을 때 어떻게 처리해야할지.
            //API 31을 대상으로 한다면 FLAG_MUTABLE과 FLAG_IMMUTABLE 중 하나로 지정.
            val replyPendingIntent = PendingIntent.getBroadcast(
                this, 30, replyIntent, PendingIntent.FLAG_MUTABLE
            )
            builder.addAction(
                NotificationCompat.Action.Builder(
                    //아이콘정보
                    R.drawable.send,
                    //엑션 문자열
                    "답장",
                    //사용자가 액션 클릭했을 때 이벤트를 위한 PendingIntent 객체
                    replyPendingIntent
                ).addRemoteInput(remoteInput).build()//원격 입력
            )//이벤트 등록

            //알림갱신  - 알림에 글을 잘 받았다는 신호를 보내는 것것
            manager.notify(11, builder.build())
        }
    }
}