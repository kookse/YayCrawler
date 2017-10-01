package com.taobao.pamirs.schedule.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType;

@SpringApplicationContext({ "schedule.xml" })
public class InitialDemoConfigData extends UnitilsJUnit4 {
	protected static transient Logger log = LoggerFactory
			.getLogger(InitialDemoConfigData.class);
	@SpringBeanByName
	TBScheduleManagerFactory scheduleManagerFactory;

	public void setScheduleManagerFactory(
			TBScheduleManagerFactory tbScheduleManagerFactory) {
		this.scheduleManagerFactory = tbScheduleManagerFactory;
	}

	@Test
	public void initialConfigData() throws Exception {
		String baseTaskTypeName = "DemoTask6";
		while(this.scheduleManagerFactory.isZookeeperInitialSucess() == false){
			Thread.sleep(1000);
		}
		scheduleManagerFactory.stopServer(null);
		Thread.sleep(1000);
		try {
			this.scheduleManagerFactory.getScheduleDataManager()
					.deleteTaskType(baseTaskTypeName);
		} catch (Exception e) {

		}
		// 创建任务调度DemoTask的基本信息
		ScheduleTaskType baseTaskType = new ScheduleTaskType();
		baseTaskType.setBaseTaskType(baseTaskTypeName);
		baseTaskType.setDealBeanName("demoTaskBean");
		baseTaskType.setHeartBeatRate(2000);
		baseTaskType.setFetchDataNumber(10);
		baseTaskType.setJudgeDeadInterval(10000);
		baseTaskType.setSleepTimeNoData(10000);
		baseTaskType.setProcessorType("SLEEP");
		baseTaskType.setTaskParameter("AREA=杭州,YEAR>30");
		baseTaskType.setTaskItems(ScheduleTaskType.splitTaskItem(
				"0:{TYPE=A,KIND=1},1:{TYPE=A,KIND=2},2:{TYPE=A,KIND=3},3:{TYPE=A,KIND=4}," +
						"4:{TYPE=A,KIND=5},5:{TYPE=A,KIND=6}"));
		this.scheduleManagerFactory.getScheduleDataManager()
				.createBaseTaskType(baseTaskType);
		log.info("创建调度任务成功:" + baseTaskType.toString());

		// 创建任务DemoTask的调度策略
		String taskName = baseTaskTypeName + "$TEST";
		String strategyName = baseTaskTypeName +"-Strategy";
		try {
			this.scheduleManagerFactory.getScheduleStrategyManager()
					.deleteMachineStrategy(strategyName,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ScheduleStrategy strategy = new ScheduleStrategy();
		strategy.setStrategyName(strategyName);
		strategy.setKind(ScheduleStrategy.Kind.Schedule);
		strategy.setTaskName(taskName);
		strategy.setTaskParameter("中国");

		strategy.setNumOfSingleServer(1);
		strategy.setAssignNum(10);
		strategy.setIPList("127.0.0.1".split(","));
		this.scheduleManagerFactory.getScheduleStrategyManager()
				.createScheduleStrategy(strategy);
		log.info("创建调度策略成功:" + strategy.toString());

		while(true){}
	}

	@Test
	public void deleteTaskType() throws Exception{
		while(this.scheduleManagerFactory.isZookeeperInitialSucess() == false){
			Thread.sleep(1000);
		}
		scheduleManagerFactory.stopServer(null);
		String baseTaskTypeName = "DemoTask3";
		String taskName = baseTaskTypeName + "$TEST";
		String strategyName = baseTaskTypeName +"-Strategy";
		try {
			this.scheduleManagerFactory.getScheduleDataManager()
					.deleteTaskType(baseTaskTypeName);
			this.scheduleManagerFactory.getScheduleStrategyManager()
					.deleteMachineStrategy(strategyName,true);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
	}
}
