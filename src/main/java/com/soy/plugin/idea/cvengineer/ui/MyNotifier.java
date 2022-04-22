package com.soy.plugin.idea.cvengineer.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

import javax.annotation.Nullable;

/**
 * 通知工具类
 * @author zhengsy
 * @since 2022-04-22
 */
public class MyNotifier {

    /**
     * 通知成功
     * @param project 对应的项目，会显示到指定项目的窗口，可为null。
     * @param content 内容
     */
    public static void notifySuccess(@Nullable Project project,
                                   String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Custom Notification Group")
                .createNotification(content, NotificationType.INFORMATION)
                .notify(project);
    }
}
