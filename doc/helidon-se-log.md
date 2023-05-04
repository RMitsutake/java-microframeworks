# Helidon(SE) チュートリアル
## 資料

公式
https://oracle-japan.github.io/ocitutorials/cloud-native/helidon-se-for-beginners/

Helidon概要（SE/MP、他のフレームワークとの比較）
https://oracle-japan.github.io/ocidocs/solutions/cloud%20native/helidon-overview/
## やったこと
### 開発環境
VSCode dev container
Java17
maven
```
$ mvn --version
Apache Maven 3.9.1 (2e178502fcdbffc201671fb2537d0cb4b4cc58f8)
Maven home: /usr/local/sdkman/candidates/maven/current
Java version: 17.0.7, vendor: Microsoft, runtime: /usr/lib/jvm/msopenjdk-current
```

Helidon CLIのインストール
```
curl -O https://helidon.io/cli/latest/linux/helidon
chmod +x ./helidon
sudo mv ./helidon /usr/local/bin/
```

helidonコマンド実行してプロジェクトのひな型作成
```
helidon init
```
チュートリアルページと同じくすべてデフォルト
項目	入力パラメータ	備考
Helidon version (default: 3.x.x):	(そのままEnter)	デフォルトが最新なので、そのままEnter
Helidon flavor	(そのままEnter)	EditionとしてSEを選択(デフォルト)
Select archetype	2	databaseを選択(Database)
Select a JSON library	(そのままEnter)	JSON-P(デフォルト)
Select a Database Server	(そのままEnter)	H2
Project groupId	(そのままEnter)	プロジェクトグループID。今回はデフォルト。
Project artifactId	(そのままEnter)	プロジェクトのアーティファクトID。今回はデフォルト。
Project version	(そのままEnter)	プロジェクトのバージョン。今回はデフォルト。
Java package name	(そのままEnter)	Javaのパッケージ名。今回はデフォルト。
Start development loop?	(そのままEnter)	development loopの実施有無。今回はなし。

アプリケーションのビルド
```
mvn package
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
出たのでビルド成功。以下jarファイルができている
database-se/target/database-se.jar

サンプルアプリケーションの実行
jarの起動
```
java -jar target/database-se.jar

```

```
 $ java -jar target/database-se.jar 
2023.05.02 11:22:57 INFO io.helidon.common.LogConfig Thread[main,5,main]: Logging at initialization configured using classpath: /logging.properties
2023.05.02 11:22:58 INFO com.zaxxer.hikari.HikariDataSource Thread[main,5,main]: h2 - Starting...
2023.05.02 11:22:58 INFO com.zaxxer.hikari.HikariDataSource Thread[main,5,main]: h2 - Start completed.
2023.05.02 11:22:59 INFO io.helidon.common.HelidonFeatures Thread[features-thread,5,main]: Helidon SE 3.2.0 features: [Config, Db Client, Fault Tolerance, Health, Metrics, Tracing, WebServer]
2023.05.02 11:22:59 INFO io.helidon.webserver.NettyWebServer Thread[nioEventLoopGroup-2-1,10,main]: Channel '@default' started: [id: 0x0db92446, L:/0.0.0.0:8080]
Database here : http://localhost:8080/pokemon
```
ブラウザ起動する
http://localhost:8080/はhttp://localhost:8080/

http://localhost:8080/pokemon
は結果あり
```
[{"id":1,"idType":12,"name":"Bulbasaur"},{"id":2,"idType":10,"name":"Charmander"},{"id":3,"idType":11,"name":"Squirtle"},{"id":4,"idType":7,"name":"Caterpie"},{"id":5,"idType":7,"name":"Weedle"},{"id":6,"idType":3,"name":"Pidgey"}]
```
http://localhost:8080/pokemon/6
```
{"name":"Pidgey","id_type":3,"id":6}
```
Controllerが無いと思ったらServiceにURLマッピングが書いてあった
ちなみにREADME.mdにもリクエスト一覧がある。

メトリクスの取得
これがあるとkubernetesとかロードバランサーとかで使うときに良いのだと思う。
Helidonが実装しているEclipse MicroProfileのMetrics仕様


初期ファイルの感想
- DockerFileがある。コンテナ上での実行が前提になっている
- src/mainの下がファイルが横並びになっている。1プロジェクト1リクエスト全前提の作りか？これは複数クラス入れると起動が遅くなるということなのか。
- 初期エンティティ/リクエスト用のテストコードがついている。Spring Bootのテンプレートだと空のテストしか無いからずいぶんよい。

### アプリケーションのカスタマイズ
Entityにweightプロパティを入れてみた
H2の初期化スクリプトが失敗しているっぽい。ターミナルでH2のDBを見る方法、初期化時のSQLデバッグ出力の方法を調べてみる
→Copilotのaut bug fixでDB初期データのjsonかjsonファイルを読み込むコードのどちらかが変わっていたのが原因。修正済
ついでにDBのSQL出力の方法を調べてlogging.propertiesに追加

### プログラム実行するDocker環境の作成
VSCodeのdev container上でビルドするのはややこしそうなのでローカルPC上でビルドする
本運用時はGithub Actionsでビルド→Dockerリポジトリにpushする仕組みを作るところ

ローカルのgitでソースをpull
Dockerファイルがあるパス（database-se)でビルドコマンド実行
```
docker build -t database-se .
```
ビルド成功したのでイメージの状態確認
```
> docker images database-se
REPOSITORY    TAG       IMAGE ID       CREATED          SIZE
database-se   latest    7e666b397a0e   20 seconds ago   417MB
```
コンテナ起動8080ポートをローカルの9000にフォワーディング
```
docker run -d -p 9000:8080 database-se
```
ブラウザで以下URLアクセスして実行できているのを確認
http://localhost:9000/pokemon


## 次にやること
docker-composeでDB、Web、キャッシュサーバー分けて実行する構成

### Java 軽量フレームワークの比較

#### MicroProfile based
- Helidon (Oracle)
- payara?
- Open Liberty
- Helidon MP
→チュートリアルがいきなりOracle Cloud DB使っていたので後回しにする
- Quarkus

#### MicroFrameworks
- javalin
- Spark
- java micronaut
- Helidon SE

####  違いそうなもの Spring Cloud
> Spring Cloud は、開発者が分散システムの一般的なパターン（構成管理、サービスディスカバリ、サーキットブレーカー、インテリジェントルーティング、マイクロプロキシ、コントロールバス、ワンタイムトークン、グローバルロック、リーダー選出、分散セッション、クラスタ状態など）を素早く構築するためのツールを提供します。
マイクロサービスのベースに使えそうだが、機能が多いのでマイクロフレームワークっぽくない？
