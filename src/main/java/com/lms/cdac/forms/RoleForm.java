package com.lms.cdac.forms;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleForm {
	@NotBlank(message = "id  is required")
      private  String userid;
	@NotBlank(message = "role is required")
	private String role;
}
