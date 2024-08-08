package com.rajput.socialmediasaver.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.widget.Toast
import com.rajput.socialmediasaver.R
import java.io.File

class Utils(private var context: Context) {
    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    companion object {
        var customDialog: Dialog? = null

        var RootDirectoryWhatsappShow: File = File(
            Environment.getExternalStorageDirectory().toString() + "/Download/StatusSaver/Whatsapp"
        )


        var PrivacyPolicyUrl: String = "https://rajputdevteam.blogspot.com/p/privacy-policy.html"

        fun setToast(_mContext: Context?, str: String?) {
            val toast = Toast.makeText(_mContext, str, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }

        fun createFileFolder() {
            if (!RootDirectoryWhatsappShow.exists()) {
                RootDirectoryWhatsappShow.mkdirs()
            }
        }


        fun shareImage(context: Context, filePath: String?) {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, context.resources.getString(R.string.share_txt))
                val path =
                    MediaStore.Images.Media.insertImage(context.contentResolver, filePath, "", null)
                val screenshotUri = Uri.parse(path)
                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                intent.setType("image/*")
                context.startActivity(
                    Intent.createChooser(
                        intent,
                        context.resources.getString(R.string.share_image_via)
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun shareImageVideoOnWhatsapp(context: Context, filePath: String?, isVideo: Boolean) {
            val imageUri = Uri.parse(filePath)
            val shareIntent = Intent()
            shareIntent.setAction(Intent.ACTION_SEND)
            shareIntent.setPackage("com.whatsapp")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "")
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            if (isVideo) {
                shareIntent.setType("video/*")
            } else {
                shareIntent.setType("image/*")
            }
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                context.startActivity(shareIntent)
            } catch (e: Exception) {
                setToast(context, context.resources.getString(R.string.whatsapp_not_installed))
            }
        }

        fun shareVideo(context: Context, filePath: String?) {
            val mainUri = Uri.parse(filePath)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.setType("video/mp4")
            sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri)
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.no_app_installed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        fun RateApp(context: Context) {
            val appName = context.packageName
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "market://details?id=$appName"
                        )
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "http://play.google.com/store/apps/details?id=$appName"
                        )
                    )
                )
            }
        }

        fun MoreApp(context: Context) {
            val appName = context.packageName
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "market://details?id=$appName"
                        )
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "http://play.google.com/store/apps/details?id=$appName"
                        )
                    )
                )
            }
        }

        fun ShareApp(context: Context) {
            val appLink = """
                 
                 https://play.google.com/store/apps/details?id=${context.packageName}
                 """.trimIndent()
            val sendInt = Intent(Intent.ACTION_SEND)
            sendInt.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            sendInt.putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.share_app_message) + appLink
            )
            sendInt.setType("text/plain")
            context.startActivity(Intent.createChooser(sendInt, "Share"))
        }

        fun OpenApp(context: Context, Package: String?) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(
                Package!!
            )
            if (launchIntent != null) {
                context.startActivity(launchIntent)
            } else {
                setToast(context, context.resources.getString(R.string.app_not_available))
            }
        }

        fun infoDialog(context: Context, title: String?, msg: String?) {
            AlertDialog.Builder(context).setTitle(title)
                .setMessage(msg)
                .setPositiveButton(
                    context.resources.getString(R.string.ok)
                ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }.create().show()
        }
    }
}