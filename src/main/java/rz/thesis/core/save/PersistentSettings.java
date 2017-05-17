package rz.thesis.core.save;

import java.io.Serializable;

public class PersistentSettings  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9189446109257688617L;
	private int version=0;
	
	public PersistentSettings(){
		
	}
	public PersistentSettings(int version){
		this.version=version;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

}
