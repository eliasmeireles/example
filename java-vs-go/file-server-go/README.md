# File Server GO

## Check if running

```shell
curl --location 'http://localhost:8081/api/file-server/v1/authorization' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "file-server@user.com",
    "password": "123456"
}'
```


