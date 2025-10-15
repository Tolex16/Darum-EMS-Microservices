package com.darum.ems.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


public enum Role {
  ADMIN,
  MANAGER,
  EMPLOYEE,

}
