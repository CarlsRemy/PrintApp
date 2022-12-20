# Impresion via Bluetooth en WebView

## Introduccion

El Evento de la imprecion es detonado por una clase
**[@JavascriptInterface](https://stackoverflow.com/questions/57881025/send-object-from-javascript-to-kotlin-using-webview#:~:text=private%20inner%20class%20JavascriptInterface%0A%7B%0A%20%20%20%20%40android.webkit.JavascriptInterface%0A%20%20%20%20fun%20showToast(text%3A%20String%3F)%0A%20%20%20%20%7B%0A%20%20%20%20%20%20%20%20Log.d(%22WEBVIEW%22%2C%20text)%3B%0A%20%20%20%20%7D%0A%7D)** donde podremos indicar tantas funciones como nececitemo. Este debemos añadirlo a nuestra WebView usando 
**addJavascriptInterface()**, donde a este le passamos la clase y un alias para la misma. 

En Javascript el codigo no tendria muchos cambios, ejecutarimos la funcion de la clase como si la misma estubiera en javascript, lo unico es que por recomiendacion validemos si estados usando la pagina web desde un Movil para asi evitar problemas a futuro.

**Nota:** La funcion responsable de mostrar la lista de Dispositivos emparejados es **getPrintersBluetooth()** ubicada en BluethoothPrinter/BluethoothUtils.java, Filtra utilizando **isBluetoothPrinter** que se encuentra en la linea 56. Aqui validamos que el **Device.Major** del dispositivo sea igual a Device.Major.UNCATEGORIZED o Device.Major.IMAGING.

El Device Major es como se categoriza dicho dispositivo. **[Lista Device.Major]( https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device.Major)**



### Permisos requeridos para Activar e utilizar el Bluethooth 
<code> 

	<uses-permission android:name="android.permission.BLUETOOTH" />
	<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
</code> 

### JavascriptInterface - Kotlin
<code> 
	private inner class Events {
		@JavascriptInterface
		fun Example(){

		}
	}
</code>

### WebView - Kotlin
<code> 

	wb_view.webViewClient = WebViewClient()
	wb_view.clearCache(false)

	wb_view.apply {
		loadUrl("https://Example.com")
		settings.javaScriptEnabled = true
		settings.safeBrowsingEnabled =true
		addJavascriptInterface(Events(), "Print")
	}
</code>

### Ejcucion del JavascriptInterface  - Javascript
<code> 

	/*Ejemplo de Funciojn par Validar dispositivo*/
	const isMovil = ()=>{
		let Device =['Android','webOS','iPhone','iPad','iPod','BlackBerry','Windows Phone'];
		let Movil = Device.some((A)=>{
			let Search = new RegExp(A,'i')
			return navigator.userAgent.match(Search)
		});
		
		return Movil || false
	};


	/*valiacion y Imprecion*/
	if(isMovil()){
		Print.Example()
	}else{
		// Imprecion Normal del Navegador
	}
</code>


## Estado de Conneccion 

La clase responsable de de la validacion es **NetworkConnection**, Esta la utilizo en MainActivity Linea 76. Para Utilizarla debemos indicar los siguientes permisos en **AndroidManifest**

<code> 

	<uses-permission android:name="android.permission.INTERNET"/>
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
</code>

### Ejemplo de Validacion
<code>
	networkConnection.observe(this, { isConnected ->
		if (isConnected) {
			layoutDisconnect.visibility = View.GONE
			wb_view.visibility = View.VISIBLE
			webViewSetup()
		} else {
			wb_view.visibility = View.GONE
			layoutDisconnect.visibility = View.VISIBLE
		}
	}
</code>
