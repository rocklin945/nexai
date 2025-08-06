declare namespace API {
type App = {
    id?: number;
    appName?: string;
    cover?: string;
    initPrompt?: string;
    codeGenType?: string;
    deployKey?: string;
    deployedTime?: string;
    priority?: number;
    userId?: number;
    editTime?: string;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type AppAdminUpdateRequest = {
    id: number;
    appName?: string;
    cover?: string;
    priority?: number;
  };

  type AppCreateRequest = {
    initPrompt: string;
  };

  type AppDeleteRequest = {
    id: number;
  };

  type AppDeployRequest = {
    appId: number;
  };

  type AppGetByIdRequest = {
    id: number;
  };

  type AppQueryPageListRequest = {
    pageNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    appName?: string;
    initPrompt?: string;
    codeGenType?: string;
    deployKey?: string;
    priority?: number;
    userId?: number;
    editTime?: string;
    createTime?: string;
    updateTime?: string;
  };

  type AppUpdateRequest = {
    id: number;
    appName: string;
  };

  type BaseResponseApp = {
    statusCode?: number;
    data?: App;
    message?: string;
  };

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
    userRole?: number;
  };

  type User = {
    id?: number;
    userAccount?: string;
    userPassword?: string;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: number;
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
    userRole?: number;
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
    userRole?: number;
    token?: string;
  };

  type UserLoginVO = {
    userId?: number;
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: number;
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
    userRole?: number;
  };

  type UserRegisterRequest = {
    userAccount: string;
    userPassword: string;
    checkPassword: string;
  };
}
