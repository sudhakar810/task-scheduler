package Job.Scheduler;

import java.util.Map;

public class Job {
	private int jobId;
	private String jobName;
	private Map<String,Object> data;
	
	public Job(int jobId, String jobName, Map<String, Object> data) {
		super();
		this.jobId = jobId;
		this.jobName = jobName;
		this.data = data;
	}
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	
}
