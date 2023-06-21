#!/bin/sh

FILE="doc_store.json"
if [ -e $FILE ]
then
  echo "File $FILE already exists."
else
  curl -O https://asawikigpt.blob.core.windows.net/demo/doc_store.json
  echo "The file $FILE has been downloaded."
fi

az storage file upload -s vectorstore --source $FILE --account-name $STORAGE_ACCOUNT_NAME --subscription $AZURE_SUBSCRIPTION_ID --account-key $STORAGE_ACCOUNT_KEY

