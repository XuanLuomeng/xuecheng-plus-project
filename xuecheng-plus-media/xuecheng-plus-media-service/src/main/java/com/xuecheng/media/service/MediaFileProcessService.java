package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 任务处理
 * @date 2024/3/11 15:21
 */
public interface MediaFileProcessService {
    /**
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count      获取记录数
     * @return
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * 开启一个任务
     *
     * @param id 任务id
     * @return true开启任务超过，false开启任务失败
     */
    public boolean startTask(long id);

    /**
     * 保存任务结果
     *
     * @param taskId   任务id
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误信息
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);
}
