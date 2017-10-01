package com.taobao.pamirs.schedule.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;

/**
 * 单个任务处理实现
 *
 * @author xuannan
 *
 */
public class DemoTaskBean implements IScheduleTaskDealSingle<Long> {
	protected static transient Logger log = LoggerFactory.getLogger(DemoTaskBean.class);

	private static final AtomicInteger SELECT_TASKS_COUNTER = new AtomicInteger(0);
	private static final AtomicInteger EXECUTE_TASKS_COUNTER = new AtomicInteger(0);

	public Comparator<Long> getComparator() {
		return new Comparator<Long>() {
			public int compare(Long o1, Long o2) {
				return o1.compareTo(o2);
			}

			public boolean equals(Object obj) {
				return this == obj;
			}
		};
	}

	/**
	 * 根据条件，查询当前调度服务器可处理的任务
	 * @param taskParameter 任务的自定义参数
	 * @param ownSign 当前环境名称
	 * @param taskItemNum 当前任务类型的任务队列数量
	 * @param queryCondition 当前调度服务器，分配到的可处理队列
	 * @param fetchNum 每次获取数据的数量
	 * @return
	 * @throws Exception
	 */
	public List<Long> selectTasks(String taskParameter,String ownSign, int taskItemNum,
								  List<TaskItemDefine> queryCondition, int fetchNum) throws Exception {

		if (SELECT_TASKS_COUNTER.get() == taskItemNum) {
			return null;
		}
		log.info("current count is {}, current taskItemNum is :" + taskItemNum + " fetchNum is :{}",
				SELECT_TASKS_COUNTER.incrementAndGet(), fetchNum);

		List<Long> result = new ArrayList<Long>();
		int num = fetchNum / queryCondition.size();
		Random random = new Random(System.currentTimeMillis());
		String message = "获取数据...[ownSign=" + ownSign + ",taskParameter=\"" + taskParameter +"\"]:";
		boolean isFirst = true;
		for (TaskItemDefine s : queryCondition) {
			long taskItem = Integer.parseInt(s.getTaskItemId()) * 10000000L;
			for (int i = 0; i < num; i++) {
				result.add(taskItem + random.nextLong()% 100000L);
			}
			if (isFirst == false) {
				message = message + ",";
			}else{
				isFirst = false;
			}
			message = message + s.getTaskItemId() + "{" + s.getParameter() +"}";
		}
		log.info(message);

		log.info("current taskId {} result :{}", queryCondition.get(0).getTaskItemId(), result);

		return result;
	}

	public boolean execute(Long task, String ownSign) throws Exception {
		//Thread.sleep(50);
		log.info("current execute count is :{}, 处理任务["+ownSign+"]:" + task, EXECUTE_TASKS_COUNTER.incrementAndGet());
		/*if (EXECUTE_TASKS_COUNTER.get() == 10){
			log.info("服务退出.");
			System.exit(0);
		}*/
		if (EXECUTE_TASKS_COUNTER.get() == 200) {
			log.info("任务调度完成.");
			Thread.sleep(10000L);
		}
		return true;
	}
}