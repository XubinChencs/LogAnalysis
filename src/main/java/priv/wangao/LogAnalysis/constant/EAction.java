package priv.wangao.LogAnalysis.constant;

public enum EAction {
	
	success("success", new ETest1()), 
	fail("fail", new ETest2());
	
	private String desc;
	private ATest value;
	
	private EAction(String desc, ATest value) {
		this.desc = desc;
		this.value = value;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public ATest getValue() {
		return this.value;
	}

}
