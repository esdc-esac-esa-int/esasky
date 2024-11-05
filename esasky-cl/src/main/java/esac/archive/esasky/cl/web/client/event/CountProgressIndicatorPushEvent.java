package esac.archive.esasky.cl.web.client.event;

public class CountProgressIndicatorPushEvent extends ProgressIndicatorPushEvent {
    public CountProgressIndicatorPushEvent(final String inputId, final String inputMessage, final String googleAnalyticsErrorMessage) {
        super(inputId, inputMessage, googleAnalyticsErrorMessage);
    }
}
