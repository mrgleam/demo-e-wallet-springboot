# Demo E-Wallet with spring boot and kafka

                            +-----------------+
                            |     Kafka       |
                            +-----------------+
                        /         |                \
        +----------------+     +----------------+    +-------------------+
        | USER_CREATION_ |     | TRANSACTION_   |    | POCKET_UPDATED_   |
        | TOPIC          |     | CREATION_TOPIC |    | TOPIC             |
        +----------------+     +----------------+    +-------------------+
               |                         |
               |                         v
               |               +--------------------+
               |               |   Pocket Service   |
               v               +--------------------+
       +--------------------+      |        |
       | Pocket Service:    |      |        |     
       | createWallet(msg)  |      |        +--------------------+
       +--------------------+      |                             |
                                   v                             v
                       +-------------------+           +--------------------+
                       | Update Wallets    |           | KafkaTemplate      |
                       | For Txn (msg)     |-----------| to send updates to |
                       +-------------------+           | POCKET_UPDATED_... |
                        Transactions sent               +--------------------+
                        to PocketUpdated topic
                                   |
                                   |
                                   v
                             +-----------+
                             | Database  |
                             | Wallets   |
                             +-----------+