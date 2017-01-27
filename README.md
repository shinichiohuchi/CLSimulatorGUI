CLSimulatorGUI マニュアル
================================================================================

これはコンビネータ文字列を計算して表示するためのソフトウェアです。

バージョン     : 2.1  
作者           : 大内真一  
作成日         : 2016/12/10  
最終更新日     : 2016/12/10  
連絡先         : [GitHub](https://github.com/shinichiohuchi/CLSimulatorGUI)
実行ファイル名 : CLSG.jar  
動作確認
- OS             : Windows10 Pro 64bit, LinuxBean12.04
- プロセッサ     : 2.00GHz Intel Core i7-3667U
- メモリ         : 8GB RAM
- Javaバージョン : 1.8.0-111

## 目次 ########################################################################

- [概要](#概要)
- [動作条件](#動作条件)
- [実行方法](#実行方法)
- [使い方](#使い方)
  - [コンビネータの計算](#コンビネータの計算)
  - [コンビネータの追加](#コンビネータの追加)
  - [定義済みコンビネータの編集](#定義済みコンビネータの編集)
- [既知の不具合](#既知の不具合)
- [FAQ](#FAQ)
- [更新履歴](#更新履歴)

## 概要 ########################################################################

CLSimulatorGUIはコンビネータ文字列(以下CLCode)を計算して表示するためのソフトウェ
アです。

テキストファイルに行単位で記述されたCLCodeを読み取ってタブ毎に計算結果を表示する
ことや、テキスト入力によって即座にテキストを入力し、計算結果をファイルに出力する
ことができます。

コンビネータは初期状態でSKIBCの５つが定義されています。コンビネータの定義は別フ
ァイルから読み込むことで新たに追加することができます。ただし初期で定義されている
SKIBCを上書きすることはできません。

## 動作条件 ####################################################################

本ソフトウェアを実行するにはJavaがインストールされている必要があります。もし
Javaがインストールされていない場合は、下記の手順にしたがってインストールを完了
してください。  
[Oracle Javaインストール](https://www.java.com/ja/download/help/download_options.xml)  

また、本ソフト作成時のJavaのバージョン以上のJava実行環境がインストールされてい
る必要があります。もしJavaがインストールされている環境で本ソフトが実行できな
かった場合は、下記の手順に従って最新のJavaにアップデートしてください。  
[Oracle Javaアップデート](https://java.com/ja/download/)  

それでも起動できなかった場合は、お手数ですがFAQの項目を確認した後に、README先頭
の連絡先より製作者に報告をお願いいたします。

## 実行方法 ####################################################################

Windows環境では"CLSG.jar"をダブルクリックすることで起動します。  
Linux環境ではターミナルから"java -jar CLSG.jar"と入力して実行します。  

## 使い方 ######################################################################

### コンビネータの計算 #########################################################

1. 画面上部のテキスト入力欄にCLCodeを入力して"追加"ボタンを押すか、ENTERキーを押
   してください。打ち込んだCLCodeの計算結果が画面中央のタブに追加されます。

2. テキストファイルに記述したCLCodeを読み込む場合は"ファイル"メニューから"ファイ
   ルを開く"を選択し、テキストファイルを選択してください。読み込むテキストファイ
   ル内でのCLCodeの読み取りは行単位で行われます。

3. 計算結果をテキストに保存したい場合、"ファイル"メニューから別名で保存を選択し
   てください。現在選択中のタブ(表示されているタブ)のテーブルのレコードがすべて
   テキストに保存されます。

### コンビネータの追加 #########################################################

"ファイル"メニューから"定義ファイルを開く"を選択してください。

### 定義済みコンビネータの編集 #################################################

1. "ファイル"メニューから"定義ファイルを編集"を選択してください。
2. 画面左側の編集テキストエリアにフォーマットを記述してください。
3. 更新ボタンをクリックするか、Ctrl + Enterを押してください。
4. 記述の仕方に不正がなければOKボタンをクリック可能になります。
   編集内容を確認してOKボタンを押してください。
5. メインウインドウの定義済みコンビネータを確認してください。

## 既知の不具合 ################################################################

- コンビネータを追加定義する場合、指定できる引数の上限は現バージョンでは9までと
  なっています。これは数値の桁数を考慮にいれずに作成したことに起因する問題です。
  今後修正を予定しています。

- 追加定義したコンビネータ名に数字を使用すると予期せぬ結果を返す場合があります。
  対処法としては、コンビネータの名前に数字を使わないようにしてください。

## FAQ #########################################################################

- Q. 起動できません。  
  A. ご利用の環境にJavaがインストールされているか、また動作環境に記載している
  Javaバージョン以上のJavaがインストールされているか確認してください。

  それでも起動できなかった場合は、実行ファイルと同階層のpropertiesフォルダを削除
  して実行できるか確認をしてください。

  万が一それでも起動できなければ、お手数ですがREADME先頭の連絡先に報告いただける
  と助かります。その際、起動できなくなる前に何を行ったのかといった情報をわかる範
  囲で報告いただけると助かります。

## 更新履歴 ####################################################################

Ver2.0:
- プログラム公開

Ver2.1:
- テーブルの右クリックコンテキストメニューに木構造ビューとXML出力を追加
- テーブルをダブルクリックすると選択レコードの木構造ビューを表示する機能を追加
- 先頭のコンビネータが計算不可能になると、計算可能でも放置されていた後続の括弧で
  括られたCLTermも計算可能に変更
- 設定変更画面を追加
- 多言語化に対応し、日本語と英語を切り替えられるように変更
- メニューにXMLファイル入出力を追加
