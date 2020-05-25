package com.github.bluecatlee.common.websocket;

import com.github.bluecatlee.common.restful.RestResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/websocket/{ID}")
public class WebSocketSession {

	private static Logger log = LoggerFactory.getLogger(WebSocketSession.class);

	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。  volatile
	private static int onlineCount = 0;

	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	private static Map<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<String, List<WebSocketSession>>(
			128);

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;
	// 用户token
	private String token;
	// 用户id
	private String empId;

	private static ApplicationContext applicationContext;

	public static void setApplicationContext(ApplicationContext applicationContext) {
		WebSocketSession.applicationContext = applicationContext;
	}

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(@PathParam("ID") String token, Session session) {
		if (StringUtils.isEmpty(token)) {
			this.close(session);
			return;
		}

		// 根据token获取empId todo
		this.empId = "";
		this.session = session;
		List<WebSocketSession> userSessions = sessions.get(empId);
		if (userSessions == null || userSessions.size() == 0) {
			userSessions = new ArrayList<WebSocketSession>();
			addOnlineCount(); // 在线数加1
			sessions.put(empId, userSessions);
		}
		userSessions.add(this);
		log.info("有新连接加入！当前在线人数为" + getOnlineCount());

		// try {
		// 	this.getHistoryMsg(emp, userSessions);// 发送遗留消息
		// } catch (Exception e) {
		// 	log.error("websocket IO异常：", e);
		// }
	}

	/**
	 * 获取遗留消息并发送
	 */
	// private void getHistoryMsg(LoginEmp emp, List<WebSocketSession> userSessions) {
		// IErpNotifyService notifyService = applicationContext.getBean(ErpNotifyService.class);
		// QueryMessageParam param = new QueryMessageParam();
		// param.setInfoId(emp.getInfoId());
		// param.setEmpId(emp.getEmpId());
		// List<Message> msgs = notifyService.queryNotReadMessage(param);
		// try {
		// 	if (msgs == null || msgs.size() == 0) {
		// 		return;
		// 	}
		// 	for (WebSocketSession sess : userSessions) {
		// 		for (Message msg : msgs) {
		// 			RestResult res = RestResult.SUCCESS().object(msg).message("接收成功").build();
		// 			sess.notify(JsonUtils.toJson(res));
		// 		}
		// 	}
		// } catch (Exception e) {
		// 	e.printStackTrace();
		// }
	// }

	/**
	 * 收到客户端消息后调用的方法
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("来自客户端的消息:" + message);
		for (Entry<String, List<WebSocketSession>> entry : sessions.entrySet()) {
			List<WebSocketSession> userSessions = entry.getValue();
			for (WebSocketSession socketSession : userSessions) {
				try {
					socketSession.notify(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 关闭连接
	 * 
	 * @param sess
	 */
	private void close(Session sess) {
		try {
			sess.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(@PathParam("ID") String token) {
		if (StringUtils.isEmpty(token)) {
			return;
		}
		
		String empId = "";
		List<WebSocketSession> userSessions = sessions.get(empId);
		if(userSessions == null || userSessions.size() == 0)
			return;
		
		int index = -1;
		for(int i = 0;i< userSessions.size();i++) {
			WebSocketSession us = userSessions.get(i);
			if(us.getToken().equals(token)) {
				index = i;
				break;
			}
		}
		
		if(index != -1) {
			userSessions.remove(index);
		}
		if(userSessions.size() == 0) {
			sessions.remove(empId);
		}

		subOnlineCount();
		log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 发生异常时处理
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		log.error("发生错误");
		error.printStackTrace();
	}

	/**
	 * 当前session发送
	 * 
	 * @param message
	 * @throws IOException
	 */
	private void notify(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}

	// /**
	//  * 指定客户端发送
	//  *
	//  * @throws IOException
	//  */
	// public static boolean nofity(NotifyBody body) throws IOException {
	// 	String token = body.getToken();
	// 	String message = body.getMessage();
	//
	// 	List<WebSocketSession> userSessions = sessions.get(body.getEmpId());
	// 	if (userSessions == null || userSessions.size() == 0) {
	// 		return false;
	// 	}
	//
	// 	for (WebSocketSession socketSession : userSessions) {
	// 		try {
	// 			RestResult res = RestResult.SUCCESS().object(message).message("接收成功").build();
	// 			socketSession.notify(JsonUtils.toJson(res));
	// 		} catch (Exception e) {
	// 			e.printStackTrace();
	// 			continue;
	// 		}
	// 	}
	// 	return true;
	// }

	/**
	 * 群发消息
	 */
	public static void notifyAll(String message) throws IOException {
		for (Entry<String, List<WebSocketSession>> entry : sessions.entrySet()) {
			List<WebSocketSession> userSessions = entry.getValue();
			for (WebSocketSession socketSession : userSessions) {
				try {
					socketSession.notify(message);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		WebSocketSession.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocketSession.onlineCount--;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

}