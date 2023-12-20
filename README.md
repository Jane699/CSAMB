## Class Specific 实验文档

## 1. 目录结构说明
`Run`: 实验启动点，实验配置和组合

`exp_run`: 实验并行与运行流设置

`exp`: 实验细节设置

`gcs`: 核心算法实现

`gcs_common`: 核心算法的通用函数和类定义

`common`: 算法无关的通用函数

## 2. 实验控制流程

`Run` --> `exp_run` --> `exp` --> `gcs` --> `*algorithm`