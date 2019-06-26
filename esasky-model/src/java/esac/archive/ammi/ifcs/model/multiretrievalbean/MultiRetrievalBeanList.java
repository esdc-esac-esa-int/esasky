package esac.archive.ammi.ifcs.model.multiretrievalbean;

import java.util.LinkedList;
import java.util.List;

public class MultiRetrievalBeanList {
	 
	private List<MultiRetrievalBean> multiRetrievalBeanList = new LinkedList<MultiRetrievalBean>();

	public List<MultiRetrievalBean> getMultiRetrievalBeanList() {
		return multiRetrievalBeanList;
	}

	public void setMultiRetrievalBeanList(
			List<MultiRetrievalBean> multiRetrievalBeanList) {
		this.multiRetrievalBeanList = multiRetrievalBeanList;
	}
	
	public void add(MultiRetrievalBean bean){
		multiRetrievalBeanList.add(bean);
	}
	
}
