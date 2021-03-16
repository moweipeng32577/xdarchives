package com.wisdom.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;

/**
 * Created by tanly on 2017/11/22 0022.
 */
@WebListener
public class PlanListener extends HttpServlet implements ServletContextListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		CopyJacodDLL read = new CopyJacodDLL();
		new Thread(read).start();// 用线程处理以防占用启动时间

		// int delaytime = 10 * 60 * 1000;//默认10分钟
		// String delay = ConfigValue.getValue("const.listener.delay");//延时
		// if (!"".equals(delay.trim())) {
		// delaytime = Integer.parseInt(delay) * 1000;
		// }
		// int timeperiod = 3 * 60 * 1000;//默认3分钟
		// String period = ConfigValue.getValue("const.listener.period");//维持多久
		// if (!"".equals(period.trim())) {
		// timeperiod = Integer.parseInt(period) * 1000;
		// }
		// String hour = ConfigValue.getValue("const.listener.hour");//什么时间段执行
		// hour = "".equals(hour.trim()) ?
		// "22" : hour;//默认22点
		//
		// timerconvert = new java.util.Timer(true);
		// timerconvert.schedule(new ConvertListener(hour), delaytime,
		// timeperiod);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// timerconvert.cancel();
	}
}

class CopyJacodDLL implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(PlanListener.class);

	private static String javaHome = "";
	private static final String JAVA_HOME = "java.home";
	static {
		javaHome = System.getProperty("java.home");
		java.security.Security.setProperty(JAVA_HOME,
				((javaHome == null || javaHome.trim().isEmpty()) ? "" : javaHome));
	}

	@Override
	public void run() {
		try {
			String arch = System.getProperty("os.arch");
			if (arch != null) {
				String classPath = this.getClass().getResource("/").getPath();
				classPath = java.net.URLDecoder.decode(classPath, "utf-8");

				String fileName;
				if (arch.contains("64")) {
					fileName = "jacob-1.18-x64.dll";
				} else {
					fileName = "jacob-1.18-x86.dll";
				}
				File source = new File(classPath + "/" + fileName);
				File target = new File(javaHome + "/bin/" + fileName);
				if (!target.exists()) {
					FileUtils.copyFile(source, target);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}