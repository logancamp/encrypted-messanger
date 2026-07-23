# encrypted-messanger
This is a Java Spring project that impliments end to end encryption using a local h2 database and Spring OOP backend. This application does not currently have forward secrecy however.

# Use:
1. Setup a docker vault for your dev username and password for h2

```
docker exec -it vault-messengerapp sh

export VAULT_TOKEN="00000000-0000-0000-0000-000000000000"
export VAULT_ADDR="http://127.0.0.1:8200"

vault kv put secret/MessageApp messengerdb.username=testuser messengerdb.password=password
```

2. Run backend
3. Run client
