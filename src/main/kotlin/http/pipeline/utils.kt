package http.pipeline


fun ClientScope.config(block: ClientScope.() -> Unit): ClientScope = CallScope(this).apply(block)