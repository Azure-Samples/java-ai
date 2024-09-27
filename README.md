# Used  - An AI powered app, that automtically generates posts on selling platforms. All you need to take is a picture.

Run the sample locally or within GitHub CodeSpaces.
You'll need an [Azure Account](https://aka.ms/az-free) and [Azure OpenAI](https://learn.microsoft.com/azure/ai-services/openai/).


## First, start Eureka in an extra bash to enable Service Discovery

```bash
cd src/eureka-server/
./mvnw spring-boot:run
```

## Install azcli and login

```bash
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
az login --use-device-code
```

## Run the deployment

```bash
./deploy.sh
```

You might have to run `chmod +x deploy.sh` first to set the access correclty.
