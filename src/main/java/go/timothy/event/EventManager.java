package go.timothy.event;

import go.timothy.constant.EventEnum;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author TimothyZz
 * @description 事件管理器
 * @program my-spider
 * @date 2018-10-12 14:36
 **/
public class EventManager {
    /**
     * 所有事件
     */
    private static final Map<EventEnum, List<Consumer>> events = new HashMap<>(8);

    /**
     * 注册监听事件
     *
     * @param eventEnum
     * @param callback
     * @return void
     * @author TimothyZz
     * @date 2018/10/12 14:56
     */
    public static void registerEvent(EventEnum eventEnum, Consumer<Object> callback) {
        List<Consumer> callBacks = events.getOrDefault(eventEnum, new ArrayList<>());
        callBacks.add(callback);
    }

    /**
     * 触发事件
     *
     * @param eventEnum
     * @param o
     * @return void
     * @author TimothyZz
     * @date 2018/10/12 15:33
     */
    public static void triggerEvent(EventEnum eventEnum, Object o) {
        Optional.ofNullable(events.get(eventEnum)).ifPresent(callbacks -> callbacks.forEach(callback -> callback.accept(o)));
    }

}
