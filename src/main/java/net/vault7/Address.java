package net.vault7;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "Addresses")
@CompoundIndexes({
    @CompoundIndex(name = "cmpidx", def = "{'street':1, 'number':-1}")
})
@Data
@Builder
class Address {

  @Id
  private String id;
  private LocalDateTime insertedTime;

  @Indexed
  private String street;
  private String number;
  private String zipCode;
  private String city;
  private String state;
  private String countryCode;
  private String continentCode;

  static Address createRandom() {
    return Address.builder()
        .id(RandomValues.string())
        .insertedTime(LocalDateTime.now())
        .street(RandomValues.string())
        .number(RandomValues.string())
        .zipCode(RandomValues.string())
        .city(RandomValues.string())
        .state(RandomValues.string())
        .countryCode(RandomValues.string())
        .continentCode(RandomValues.string())
        .build();
  }
}

