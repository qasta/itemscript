## For synchronous get operations ##

```
if (the URL does not contain a query string) {
  SyncGetConnector
} else {
  if (the query string contains one of the keys from SyncBrowseConnector AND the connector implements SyncBrowseConnector) {
    SyncBrowseConnector
  } else if (the query string contains the "dump" key AND the connector implements SyncDumpConnector) {
    SyncDumpConnector
  } else {
    SyncQueryConnector
  }
}
```

## For synchronous put operations ##

```
if (the URL does not contain a query string) {
  SyncPutConnector
} else {
  if (the query string contains the "load" key AND the connector implements SyncLoadConnector) {
    SyncLoadConnector
  } else {
    SyncPostConnector
  }
}
```

## For synchronous remove operations ##

```
SyncPutConnector
```

## For asynchronous get operations ##

```
AsyncGetConnector
```

## For asynchronous put operations ##

```
if (URL does not have a query string) {
  AsyncPutConnector
} else {
  AsyncPostConnector
}
```

## For asynchronous remove operations ##

```
AsyncPutConnector
```