/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
@JacksonXmlRootElement(localName = "SiafEntry")
@JsonIgnoreProperties
public class SiafEntry {
	private String instrName;
	private String aperName;
	private String dDCName;
	private String aperType;
	private String aperShape;
	private int xDetSize;
	private int yDetSize;
	private float xDetRef;
	private float yDetRef;
	private int xSciSize;
	private int ySciSize;
	private float xSciRef;
	private float ySciRef;
	private double xSciScale;
	private double ySciScale;
	private double v2Ref;
	private double v3Ref;
	private double v3IdlYAngle;
	private int vIdlParity;
	private double xIdlVert1;
	private double xIdlVert2;
	private double xIdlVert3;
	private double xIdlVert4;
	private double yIdlVert1;
	private double yIdlVert2;
	private double yIdlVert3;
	private double yIdlVert4;

	public String getInstrName() {
		return instrName;
	}

	public void setInstrName(String instrName) {
		this.instrName = instrName;
	}

	public String getAperName() {
		return aperName;
	}

	public void setAperName(String aperName) {
		this.aperName = aperName;
	}

	public String getDdcName() {
		return dDCName;
	}

	public void setDdcName(String ddcName) {
		this.dDCName = ddcName;
	}

	public String getAperType() {
		return aperType;
	}

	public void setAperType(String aperType) {
		this.aperType = aperType;
	}

	public String getAperShape() {
		return aperShape;
	}

	public void setAperShape(String aperShape) {
		this.aperShape = aperShape;
	}

	public int getxDetSize() {
		return xDetSize;
	}

	public void setxDetSize(int xDetSize) {
		this.xDetSize = xDetSize;
	}

	public int getyDetSize() {
		return yDetSize;
	}

	public void setyDetSize(int yDetSize) {
		this.yDetSize = yDetSize;
	}

	public float getxDetRef() {
		return xDetRef;
	}

	public void setxDetRef(float xDetRef) {
		this.xDetRef = xDetRef;
	}

	public float getyDetRef() {
		return yDetRef;
	}

	public void setyDetRef(float yDetRef) {
		this.yDetRef = yDetRef;
	}

	public int getxSciSize() {
		return xSciSize;
	}

	public void setxSciSize(int xSciSize) {
		this.xSciSize = xSciSize;
	}

	public int getySciSize() {
		return ySciSize;
	}

	public void setySciSize(int ySciSize) {
		this.ySciSize = ySciSize;
	}

	public float getxSciRef() {
		return xSciRef;
	}

	public void setxSciRef(float xSciRef) {
		this.xSciRef = xSciRef;
	}

	public float getySciRef() {
		return ySciRef;
	}

	public void setySciRef(float ySciRef) {
		this.ySciRef = ySciRef;
	}

	public double getxSciScale() {
		return xSciScale;
	}

	public void setxSciScale(double xSciScale) {
		this.xSciScale = xSciScale;
	}

	public double getySciScale() {
		return ySciScale;
	}

	public void setySciScale(double ySciScale) {
		this.ySciScale = ySciScale;
	}

	public double getV2Ref() {
		return v2Ref;
	}

	public void setV2Ref(double v2Ref) {
		this.v2Ref = v2Ref;
	}

	public double getV3Ref() {
		return v3Ref;
	}

	public void setV3Ref(double v3Ref) {
		this.v3Ref = v3Ref;
	}

	public int getvIdlParity() {
		return vIdlParity;
	}

	public void setvIdlParity(int vIdlParity) {
		this.vIdlParity = vIdlParity;
	}

	public double getxIdlVert1() {
		return xIdlVert1;
	}

	public void setxIdlVert1(double xIdlVert1) {
		this.xIdlVert1 = xIdlVert1;
	}

	public double getxIdlVert2() {
		return xIdlVert2;
	}

	public void setxIdlVert2(double xIdlVert2) {
		this.xIdlVert2 = xIdlVert2;
	}

	public double getxIdlVert3() {
		return xIdlVert3;
	}

	public void setxIdlVert3(double xIdlVert3) {
		this.xIdlVert3 = xIdlVert3;
	}

	public double getxIdlVert4() {
		return xIdlVert4;
	}

	public void setxIdlVert4(double xIdlVert4) {
		this.xIdlVert4 = xIdlVert4;
	}

	public double getyIdlVert1() {
		return yIdlVert1;
	}

	public void setyIdlVert1(double yIdlVert1) {
		this.yIdlVert1 = yIdlVert1;
	}

	public double getyIdlVert2() {
		return yIdlVert2;
	}

	public void setyIdlVert2(double yIdlVert2) {
		this.yIdlVert2 = yIdlVert2;
	}

	public double getyIdlVert3() {
		return yIdlVert3;
	}

	public void setyIdlVert3(double yIdlVert3) {
		this.yIdlVert3 = yIdlVert3;
	}

	public double getyIdlVert4() {
		return yIdlVert4;
	}

	public void setyIdlVert4(double yIdlVert4) {
		this.yIdlVert4 = yIdlVert4;
	}

	public double getV3IdlYAngle() {
		return v3IdlYAngle;
	}

	public void setV3IdlYAngle(double v3IdlYAngle) {
		this.v3IdlYAngle = v3IdlYAngle;
	}

}
