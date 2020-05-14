package esac.archive.esasky.cl.web.client.utility;

public final class SizeFormatter {

	public final native static String formatBytes(int bytes, int decimals) /*-{ 
        if(bytes == 0) return '0 Bytes';
        var k = 1024, 
        dm = decimals <= 0 ? 0 : decimals || 2, 
        sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'], 
        i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
	}-*/;
	
	public final native static double formatToBytes(String fileSize) /*-{ 
	    var numberString = "";
	    
        var split = fileSize.split(/[A-Za-z]/);
        for(var i = 0; i < split.length; i++) {
            if(!isNaN(split[i]) && split[i].trim().length > 0){
                numberString = split[i];
                break;
            }
        }
	    
        var sizeSuffix = fileSize.split(numberString)[1].match(/[A-Za-z]+/);
        var i = 0;
        var sizeSuffixPossibilities = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        sizeSuffixPossibilities.forEach(function (suffix){
            if(sizeSuffix && sizeSuffix[0] && sizeSuffix[0].toLowerCase().includes(suffix.toLowerCase())){
                i = sizeSuffixPossibilities.indexOf(suffix);
            }
        });
        return parseFloat(numberString) * Math.pow(1024, i);
	}-*/;
}
