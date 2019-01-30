package com.dianping.agentsdk.framework;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by runqi.wei on 2018/1/8.
 */

public class WhiteBoardMessageManager {

    public static final String WHITE_BOARD_MESSAGE_HANDLER_PREFIX = "WhiteBoard_Message_Handler_Registration_";
    public static final String WHITE_BOARD_MESSAGE_HANDLER_WITH_KEY_PREFIX = "WhiteBoard_Message_Handler_With_Key_Registration_";

    protected MessageManager<WhiteBoard.MessageHandler> messageHandlerManager;

    protected MessageManager<WhiteBoard.MessageHandlerWithKey> messageHandlerWithKeyManager;

    public WhiteBoardMessageManager() {
        messageHandlerManager = new MessageManager<>(WHITE_BOARD_MESSAGE_HANDLER_PREFIX, new MessageManager.OnMessageQueriedListener<WhiteBoard.MessageHandler>() {
            @Override
            public Object onMessageQueried(String key, Object parameter, WhiteBoard.MessageHandler handler) {
                if (handler != null) {
                    return handler.handleMessage(parameter);
                }
                return null;
            }
        });
        messageHandlerWithKeyManager = new MessageManager<>(WHITE_BOARD_MESSAGE_HANDLER_WITH_KEY_PREFIX, new MessageManager.OnMessageQueriedListener<WhiteBoard.MessageHandlerWithKey>() {
            @Override
            public Object onMessageQueried(String key, Object parameter, WhiteBoard.MessageHandlerWithKey handler) {
                if (handler != null) {
                    return handler.handleMessage(key, parameter);
                }
                return null;
            }
        });
    }

    public void onCreate() {

    }

    public void onDestroy() {
        messageHandlerManager.clear();
        messageHandlerWithKeyManager.clear();
    }

    /**
     * For Test
     */
    MessageManager<WhiteBoard.MessageHandler> getMessageHandlerManager() {
        return messageHandlerManager;
    }

    /**
     * For Test
     */
    MessageManager<WhiteBoard.MessageHandlerWithKey> getMessageHandlerWithKeyManager() {
        return messageHandlerWithKeyManager;
    }

    public ArrayList<Object> queryMessage(@NonNull String key, Object parameter) {
        ArrayList<Object> res = new ArrayList<>();
        if (messageHandlerManager != null) {
            res.addAll(messageHandlerManager.queryMessage(key, parameter));
        }
        if (messageHandlerWithKeyManager != null) {
            res.addAll(messageHandlerWithKeyManager.queryMessage(key, parameter));
        }
        return res;
    }

    public String registerMessageHandler(@NonNull String key, @NonNull WhiteBoard.MessageHandler handler) {
        return messageHandlerManager.registerHandler(key, handler);
    }

    public String registerMessageHandler(@NonNull String key, @NonNull WhiteBoard.MessageHandlerWithKey handler) {
        return messageHandlerWithKeyManager.registerHandler(key, handler);
    }

    public void removeMessageHandler(@NonNull String id) {
        messageHandlerManager.removeHandler(id);
        messageHandlerWithKeyManager.removeHandler(id);
    }

    public void removeMessageHandler(@NonNull String key, @NonNull WhiteBoard.MessageHandler handler) {
        messageHandlerManager.removeHandler(key, handler);
    }

    public void removeMessageHandler(@NonNull WhiteBoard.MessageHandler handler) {
        messageHandlerManager.removeHandler(handler);
    }

    public void removeMessageHandler(@NonNull String key, @NonNull WhiteBoard.MessageHandlerWithKey handler) {
        messageHandlerWithKeyManager.removeHandler(key, handler);
    }

    public void removeMessageHandler(@NonNull WhiteBoard.MessageHandlerWithKey handler) {
        messageHandlerWithKeyManager.removeHandler(handler);
    }
}
