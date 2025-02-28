/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.ifcs.model.multiretrievalbean;

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
