declare namespace API {
  type BaseResponseBoolean = {
    statusCode?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseLong = {
    statusCode?: number;
    data?: number;
    message?: string;
  };

  type BaseResponsePageResponseUserLoginVO = {
    statusCode?: number;
    data?: PageResponseUserLoginVO;
    message?: string;
  };

  type BaseResponseString = {
    statusCode?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser = {
    statusCode?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserLoginResponse = {
    statusCode?: number;
    data?: UserLoginResponse;
    message?: string;
  };

  type BaseResponseUserLoginVO = {
    statusCode?: number;
    data?: UserLoginVO;
    message?: string;
  };

  type BaseResponseVoid = {
    statusCode?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type deleteUserParams = {
    id: number;
  };

  type getByIdParams = {
    id: number;
  };

  type PageResponseUserLoginVO = {
    list?: UserLoginVO[];
    total?: number;
    pageNum?: number;
    pageSize?: number;
    pages?: number;
    hasNext?: boolean;
    hasPrevious?: boolean;
  };

  type UpdateUserRequest = {
    id?: number;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
  };

  type User = {
    id?: number;
    userAccount?: string;
    userPassword?: string;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    editTime?: string;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type UserCreateRequest = {
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserLoginRequest = {
    userAccount: string;
    userPassword: string;
  };

  type UserLoginResponse = {
    userId?: number;
    userAccount?: string;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    token?: string;
  };

  type UserLoginVO = {
    userId?: number;
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    createTime?: string;
    updateTime?: string;
  };

  type UserPageQueryRequest = {
    pageNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    userName?: string;
    userAccount?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    userAccount: string;
    userPassword: string;
    checkPassword: string;
  };
}
