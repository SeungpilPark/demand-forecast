# 빌드 실행

```shell script
$ sam build
$ sam local invoke ForecastExcelFunction --event events/event.json --env-vars env/variables.json
$ sam deploy --guided 
$ sam deploy --no-confirm-changeset
```

## 스택삭제

```shell script
$ aws cloudformation delete-stack --stack-name bmart-fulfillment-forecast
```

## 컨테이너 내부 확인

```shell script
$ docker run -it --entrypoint /bin/bash forecastexcelfunction:java11-gradle-v1
```

## SAM 리소스 레퍼런스
 
https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-resource-function.html


