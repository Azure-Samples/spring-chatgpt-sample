$file = 'doc_store.json'

#If the file does not exist, download it.
if (-not(Test-Path -Path $file -PathType Leaf)) {
    try {
        Invoke-WebRequest -Uri https://asawikigpt.blob.core.windows.net/demo/doc_store.json -OutFile $file
        Write-Host "The file [$file] has been downloaded."
    }
    catch {
        throw $_.Exception.Message
    }
}
# If the file already exists, show the message and do nothing.
else {
    Write-Host "The file [$file] already exists."
}
az storage file upload -s vectorstore --source $file --account-name $Env:STORAGE_ACCOUNT_NAME --subscription $Env:AZURE_SUBSCRIPTION_ID --account-key $Env:STORAGE_ACCOUNT_KEY

