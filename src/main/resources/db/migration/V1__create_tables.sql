CREATE TABLE inbound_message (
                                 id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 name       VARCHAR(100) NOT NULL,
                                 email      VARCHAR(150) NOT NULL,
                                 message    TEXT NOT NULL,
                                 received_at TIMESTAMP NOT NULL
);

CREATE TABLE lead (
                      id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                      message_id  BIGINT NOT NULL UNIQUE,
                      title       VARCHAR(200) NOT NULL,
                      type        VARCHAR(50)  NOT NULL,
                      urgency     VARCHAR(20)  NOT NULL,
                      summary     TEXT         NOT NULL,
                      created_at  TIMESTAMP    NOT NULL,
                      CONSTRAINT fk_lead_message FOREIGN KEY (message_id)
                          REFERENCES inbound_message(id)
);

CREATE INDEX idx_lead_type ON lead (type);
CREATE INDEX idx_lead_urgency ON lead (urgency);
CREATE INDEX idx_inbound_message_email ON inbound_message (email);