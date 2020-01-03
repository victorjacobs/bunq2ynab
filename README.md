# Bunq2ynab

[![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/vjacobs/bunq2ynab.svg)](https://hub.docker.com/r/vjacobs/bunq2ynab)

Synchronizes outgoing Bunq transactions to YNAB.

## Usage

1. Add `#bunqimport` to the note of the account in YNAB you want to import transactions into
2. Create `bunq2ynab.yaml` configuration file. Example [here](bunq2ynab.sample.yaml)
3. `docker run -v $(pwd)/bunq2ynab.yaml:/app/bunq2ynab.yaml vjacobs/bunq2ynab`
4. Every minute, new transactions are imported into YNAB
