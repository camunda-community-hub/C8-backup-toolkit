# C8-backup-toolkit
Documentation to configure the backup using S3, Azure, Minio 

# Preparation : Connect the cluster to an external storage
The C8 server will be connector to a storage to save the backup file.
The storage maybe on Azure, S3, Google, Minio.

The configuration is the following:
* The container (S3, Azure, AWS) is created, and accessible.
* Different repositories are created in Elastic Search (Operate, Tasklist, Optimize, ZeebeRecord)
* The ElasticSearch's repositories are referenced in each component (Operate, Tasklist, Operate, Zeebe)
* Zeebe referenced a container

![Backup Configuration](doc/backup/ConfigurationBackup.png)
Check the procedure step by step, according the storage


The procedure to connect on each storage are 
* [Azure](doc/storage/azure/README.md)
* [AWS S3](doc/storage/AWS-S3/README.md)
* [Google](doc/storage/GCP/README.md)
* [Minio](doc/storage/minio/README.md)


# Run a backup
The documentation on the backup is
https://docs.camunda.io/docs/8.5/self-managed/operational-guides/backup-restore/backup-and-restore/

Check the [Backup procedure](doc/backup/README.md)



# Run a restore

The restore is complex, because it's not only based on REST API. Zeebe has a specific tool to restore the data.
Check the [Restoration procedure](doc/restore/README.md)

# Todo
Check the [Todo.md](Todo.md) file