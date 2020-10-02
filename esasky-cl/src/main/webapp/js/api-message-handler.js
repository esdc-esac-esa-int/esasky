class ApiMessageHandler  {

  constructor() {
    this.messageQueue = [];
  }
	getMessageFunction(){
		return this.messageFunction;
	};
	setMessageFunction(func){
		this.messageFunction = func;
	};
	getMessageQueue(){
		return this.messageQueue;
	};
	
	emptyQueue(){
		for(var i = 0; i < this.messageQueue.length; i++){
			this.messageFunction(this.messageQueue[i]);
		}
		this.messageQueue = [];
	};
};

window.ApiMessageHandler = new ApiMessageHandler ()

window.addEventListener('message', function(e){
	if(!window.ApiMessageHandler.getMessageFunction()){
		window.ApiMessageHandler.getMessageQueue().push(e);
	}else{
		window.ApiMessageHandler.getMessageFunction()(e);
	}
});