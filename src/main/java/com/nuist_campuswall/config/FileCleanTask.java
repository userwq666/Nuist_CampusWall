package com.nuist_campuswall.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nuist_campuswall.domain.enums.FileStatus;
import com.nuist_campuswall.domain.file.FileAsset;
import com.nuist_campuswall.mapper.file.FileAssetMapper;
import com.nuist_campuswall.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileCleanTask {
    private final FileService fileService;
    private final FileAssetMapper fileAssetMapper;

    @Scheduled(cron = "${app.file.clean-temp-cron}")
    public void cleanTempFiles() {
        LocalDateTime time = LocalDateTime.now().minusHours(48);  //48小时

        List<FileAsset> list = fileAssetMapper.selectList(
                Wrappers.<FileAsset>lambdaQuery()
                .eq(FileAsset::getStatus, FileStatus.TEMP)
                .lt(FileAsset::getUpdateTime, time)      //更新时间小于48小时
        );

        for (FileAsset item : list){
            fileService.deleteFileById(item.getId());
        }
    }
}