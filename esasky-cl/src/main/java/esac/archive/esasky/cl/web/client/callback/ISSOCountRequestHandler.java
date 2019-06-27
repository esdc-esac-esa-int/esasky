package esac.archive.esasky.cl.web.client.callback;

import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;

public interface ISSOCountRequestHandler {
    
    public String getSSOProgressIndicatorMessage(String ssoName, ESASkySSOObjType ssoType);
    public void showObjectNotAvailableInEsaSkyMsg(String progressIndicatorId);
}