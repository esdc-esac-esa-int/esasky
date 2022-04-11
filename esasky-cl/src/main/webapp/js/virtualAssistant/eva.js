var msg_func_input = function (cmd) {
    cmd = cmd.replace(/(\w+)(:[^/])/g, function () { return "\"" + arguments[1] + "\"" + arguments[2] });
    var command = cmd.replace(/[‘’'’‘]/g, '"');
    try {
    	webElement = document.querySelectorAll('[id^=esasky_cl]')[0]
    	cmd = JSON.parse(command);
    	cmd['msgId'] = 'eva'; 
        window.postMessage(cmd)
    } catch (error) {
        console.log(error);
    }

}

function initChat2() {
	const styleOptions = {
	        hideUploadButton: true,
	        backgroundColor: 'rgba(0, 0, 0, 0.85)',
	        sendBoxBackground: 'transparent',
	        bubbleBackground: 'rgb(97 110 124 / 32%)',
	        bubbleTextColor: '#F5F7FA',
	        markdownRespectCRLF: true,
	        bubbleBorderWidth: 0,
	        bubbleFromUserBorderWidth: 0,
	        bubbleFromUserBackground: '#20a4d8',
	        bubbleFromUserTextColor: '#ffffff',
	        sendBoxBorderTop: '1px solid #CCC',
	        subtle: '#CBD2D9',
	        sendBoxButtonColorOnFocus: '#ffffff',
	        sendBoxButtonColorOnHover: '#ffffff',
	        sendBoxTextColor: '#ffffff',
	        paddingRegular: '10px!important',
	        paddingWide: '10px!important',
	        sendBoxHeight: 50,
	        typingAnimationBackgroundImage: 'url(\'https://eva.esa.int/mgmt/assets/images/typing.gif\')',
	        typingAnimationWidth: 180,
	        bubbleMinHeight: 30,
	        suggestedActionBackground: 'transparent',
	        suggestedActionBorder: undefined, // split into 3, null
	        suggestedActionBorderColor: '#616E7C', // defaults to accent
	        suggestedActionBorderStyle: 'solid',
	        suggestedActionBoarderWidth: 1,
	        suggestedActionBorderRadius: 2,
	        suggestedActionImageHeight: 20,
	        suggestedActionTextColor: '#ffffff',
	        suggestedActionDisabledBackground: undefined, // defaults to suggestedActionBackground
	        suggestedActionHeight: 40,
	        bubbleMaxWidth: 280
	    };

    window.store = window.WebChat.createStore(
        {},
        ({ dispatch }) => next => action => {
            // connection with a bot is created, host is sent for conversation history, to know from where is comming conversation, language - to init dialog in English
            if (action.type === 'DIRECT_LINE/CONNECT_FULFILLED') {  
                dispatch({
                    type: 'WEB_CHAT/SEND_EVENT',
                    payload: {
                        name: 'webchat/join',
                        value: {
                            locale: 'en-en',
                            host: location.protocol + '//' + location.hostname
                        }
                    }
                });
            }
            // bot sends msg to the page
            if ((action.type === 'DIRECT_LINE/POST_ACTIVITY' || (action.type === 'DIRECT_LINE/INCOMING_ACTIVITY' && action.payload.activity.from.role == 'bot')) && action.payload.activity.type == "message") {
                if (action.payload.activity.text.length > 0 && action.payload.activity.text.startsWith("postMessage:")) {
                    // receives msg from bot and sends to iframe (1)
                    var cmd = action.payload.activity.text.split("postMessage:");
                    if (cmd.length == 2) {
                        postback = "";
                        msg_func_input(cmd[1]);
                        return next;
                    }
                }
           
            }
            return next(action);
        });
    // creates communication channel to ESA Sky bot
    window.directline = window.WebChat.createDirectLine({
        secret: '',
        token: '',
        domain: "https://eva.esa.int/dl/directline/aHR0cDovL2V2YWVzYXNreWJvdDA=",
        webSocket: false
    });

    // renders webchat
    window.WebChat.renderWebChat(
		{
			directLine: window.directline,
			locale: 'en',
			styleOptions,
			store: window.store,
			selectVoice: () => ({ voiceURI: 'en-GB-George-Apollo' }),
			webSpeechPonyfillFactory:  window.WebChat.createCognitiveServicesSpeechServicesPonyfillFactory({
				credentials: async (credentials = {}) => {
					const response = await fetch("https://eva.esa.int/dl/directline/aHR0cDovL2V2YWVzYXNreWJvdDA=/tokens/speech" , {
						method: 'POST',
					});
					if (response.status === 200) {
						const { subscriptionKey, region } = await response.json();
						credentials['subscriptionKey'] = subscriptionKey;
						credentials['region'] = region;
						return credentials;
					} else {
						console.log('error')
					}
				}
			})
		},
        document.getElementById('webchat')
    );
    document.querySelector('#webchat > *').focus();

    //listens messages from iframe and sends to bot (2)
    window.addEventListener("message", function (e) {
        var data = JSON.stringify(e.data);
        if (e.data.origin == "esasky" && e.data.msgId == "eva" && data != postback) {
            try {
                postback = data;
                window.store.dispatch({
                    type: 'WEB_CHAT/SEND_POST_BACK',
                    payload: { value: 'postBack:' + data }
                });
            } catch (error) {
                console.log(error);
            }
        }

    });
};

function initChat() {
	(async function () {
		if(!this.jsLoaded){
			$.getScript("js/virtualAssistant/webchat.js", initChat2)
			this.jsLoaded = true;
		}else{
		    initChat2(); //inits webchat
		}
	})().catch(err => console.error(err));
}

function clearChat() {
    window.store = null;
    window.directline = null;
    initChat();
}