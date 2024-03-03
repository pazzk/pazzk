## Pazzk

치지직(chzzk)의 후원(치즈) 알림을 받아올 수 있는 Coroutine 기반 Kotlin API

## Flow Chart
![flow-chart.png](pazzk-flow.png)

## How To Use

```kotlin
val pazzk = Pazzk("YOUR_DONATE_KEY")

pazzk.addListener { response ->
    if (response.isMessage) {
        // which is websocket message
        // such as connect, disconnect, error, etc...
        println("$response.message")
    }
    else {
        // which only for donate event
        println("$reponse.amount 원 후원")
    }
}

pazzk.connect()

// something later...

pazzk.disconnect()
```

