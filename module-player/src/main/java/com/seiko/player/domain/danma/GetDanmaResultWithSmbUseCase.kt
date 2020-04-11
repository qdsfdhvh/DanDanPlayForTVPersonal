package com.seiko.player.domain.danma

import android.net.Uri
import com.seiko.common.data.Result
import com.seiko.player.data.comments.SmbMd5Repository
import com.seiko.player.data.comments.SmbMrlRepository
import com.seiko.player.util.SmbUtils
import com.seiko.player.util.getVideoMd5
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.videolan.vlc.danma.DanmaResultBean
import timber.log.Timber
import java.io.FileNotFoundException

class GetDanmaResultWithSmbUseCase : KoinComponent {

    private val getResult: GetDanmaResultUseCase by inject()
    private val smbMd5Repo: SmbMd5Repository by inject()
    private val smbMrlRepo: SmbMrlRepository by inject()

    /**
     * @param videoUri 视频SMB路径
     * @param isMatched 是否精确匹配
     */
    suspend operator fun invoke(videoUri: Uri, isMatched: Boolean): Result<DanmaResultBean> {
        // SMB连接
        val urlValue = videoUri.toString()

        // 先从数据去查找是否与此url匹配的MD5，没有则连接SMB去获取。
        var videoMd5 = smbMd5Repo.getVideoMd5(urlValue)
        if (videoMd5 == null) {
            when(val result = getSmbVideoMd5(videoUri)) {
                is Result.Success -> {
                    videoMd5 = result.data
                    // 保存此url的MD5
                    smbMd5Repo.saveVideoMd5(urlValue, videoMd5)
                }
                is Result.Error -> return result
            }
        }

        // 加载弹幕
        return getResult.invoke(videoMd5, isMatched)
    }

    /**
     * 获取此SMB视频连接的MD5
     */
    private suspend fun getSmbVideoMd5(videoUri: Uri): Result<String> {
        // 获取SMB的账号密码
        val urlValue = videoUri.toString()
        val smbMrl = smbMrlRepo.getSmbMrl(urlValue)
            ?: return Result.Error(Exception("Not account and password with $urlValue"))
        val account = smbMrl.account
        val password = smbMrl.password

        // 获取smb路径
        val videoFile = kotlin.runCatching {
            SmbUtils.getFileWithUri(videoUri, account, password)
        }.getOrElse { error ->
            return Result.Error(error as Exception)
        }

        // 视频是否存在
        try {
            if (!videoFile.exists()) {
                return Result.Error(FileNotFoundException("Not found smbFile: $videoFile"))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }

        // 获取视频Md5，需要下载16mb资源，比较耗时。
        val start = System.currentTimeMillis()
        Timber.d("get videoMd5 with smb...")
        val videoMd5 = videoFile.getVideoMd5()
        Timber.d("get videoMd5 with smb, 耗时：${System.currentTimeMillis() - start}")

        return Result.Success(videoMd5)
    }
}