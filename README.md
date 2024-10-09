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

### Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of Microsoft
trademarks or logos is subject to and must follow
[Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship.
Any use of third-party trademarks or logos are subject to those third-party's policies.
