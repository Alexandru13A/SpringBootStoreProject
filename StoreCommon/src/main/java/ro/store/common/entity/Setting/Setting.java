package ro.store.common.entity.Setting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Setting {
  @Id
  @Column(name = "'key'", nullable = false, length = 128)
  private String key;

  @Column(nullable = false, length = 1024)
  private String value;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 45)
  private SettingCategory category;

  public Setting(String key) {
    this.key = key;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;

    Setting other = (Setting) obj;
    if (key == null) {
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;

    return true;

  }

  @Override
  public String toString() {
    return "key= " + key + ", value= " + value;
  }

}