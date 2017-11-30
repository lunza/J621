package lunza.j621.vo;

public class ImageVO {
	
	private String urlLocation;
	private String savePath;
	private String filePath;
	
	
	public ImageVO() {
		super();
	}
	public ImageVO(String urlLocation, String savePath, String filePath) {
		super();
		this.urlLocation = urlLocation;
		this.savePath = savePath;
		this.filePath = filePath;
	}
	public String getUrlLocation() {
		return urlLocation;
	}
	public void setUrlLocation(String urlLocation) {
		this.urlLocation = urlLocation;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	

}
