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

                val channelId="one-channel"
                val channelName="My Channel One"
                val channel = NotificationChannel(
                    channelId, // 첫번째 매개변수 : 채널의 식별값
                    channelName, // 두번째 매개변수 : 설정화면에 표시할 채널 이름
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "My Channel One Description"
                    setShowBadge(true)
                    //링톤 객체
                    val uri: Uri = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                    //알림음
                    setSound(uri, audioAttributes)
                    //진동 울릴 지 여부
                    enableVibration(true)

                }

                manager.createNotificationChannel(channel)
                builder= Builder(this, channelId)
            } else{
                builder = NotificationCompat.Builder(this)
            }
            builder.run {
                setSmallIcon(R.drawable.small)
                setWhen(System.currentTimeMillis())
                setContentTitle("권시경")
                setContentText("답장주세요.")
                setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.big))
            }
            val KEY_TEXT_REPLY = "key_text_reply"
            var replyLabel:String = "답장"
            var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
                setLabel(replyLabel)
                build()
            }


            val replyIntent = Intent(this, ReplyReceiver::class.java)
            val replyPendingIntent = PendingIntent.getBroadcast(
                this, 30, replyIntent, PendingIntent.FLAG_MUTABLE
            )
            builder.addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.send,
                    "답장",
                    replyPendingIntent
                ).addRemoteInput(remoteInput).build()
            )
            manager.notify(11, builder.build())
        }
    }
}