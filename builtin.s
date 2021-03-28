	.file	"builtin.c"
	.text
	.section	.rodata
	.align	2
.LC0:
	.string	"%d"
	.text
	.align	2
	.globl	getInt
	.type	getInt, @function
getInt:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	addi	a5,s0,-20
	mv	a1,a5
	lui	a5,%hi(.LC0)
	addi	a0,a5,%lo(.LC0)
	call	scanf
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	getInt, .-getInt
	.section	.rodata
	.align	2
.LC1:
	.string	"%s"
	.text
	.align	2
	.globl	print
	.type	print, @function
print:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a1,-20(s0)
	lui	a5,%hi(.LC1)
	addi	a0,a5,%lo(.LC1)
	call	printf
	nop
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	print, .-print
	.align	2
	.globl	println
	.type	println, @function
println:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a0,-20(s0)
	call	puts
	nop
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	println, .-println
	.align	2
	.globl	printInt
	.type	printInt, @function
printInt:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a1,-20(s0)
	lui	a5,%hi(.LC0)
	addi	a0,a5,%lo(.LC0)
	call	printf
	nop
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	printInt, .-printInt
	.section	.rodata
	.align	2
.LC2:
	.string	"%d\n"
	.text
	.align	2
	.globl	printlnInt
	.type	printlnInt, @function
printlnInt:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a1,-20(s0)
	lui	a5,%hi(.LC2)
	addi	a0,a5,%lo(.LC2)
	call	printf
	nop
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	printlnInt, .-printlnInt
	.align	2
	.globl	getString
	.type	getString, @function
getString:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	li	a0,1000
	call	malloc
	mv	a5,a0
	sw	a5,-20(s0)
	lw	a1,-20(s0)
	lui	a5,%hi(.LC1)
	addi	a0,a5,%lo(.LC1)
	call	scanf
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	getString, .-getString
	.align	2
	.globl	toString
	.type	toString, @function
toString:
	addi	sp,sp,-64
	sw	ra,60(sp)
	sw	s0,56(sp)
	addi	s0,sp,64
	sw	a0,-52(s0)
	sw	zero,-20(s0)
	lw	a5,-52(s0)
	bge	a5,zero,.L10
	lw	a5,-52(s0)
	neg	a5,a5
	sw	a5,-52(s0)
	li	a5,1
	sw	a5,-20(s0)
.L10:
	lw	a5,-52(s0)
	sw	a5,-24(s0)
	sw	zero,-28(s0)
.L11:
	lw	a4,-24(s0)
	li	a5,10
	div	a5,a4,a5
	sw	a5,-24(s0)
	lw	a5,-28(s0)
	addi	a5,a5,1
	sw	a5,-28(s0)
	lw	a5,-24(s0)
	bne	a5,zero,.L11
	lw	a5,-28(s0)
	addi	a4,a5,1
	lw	a5,-20(s0)
	add	a5,a4,a5
	mv	a0,a5
	call	malloc
	mv	a5,a0
	sw	a5,-36(s0)
	lw	a4,-28(s0)
	lw	a5,-20(s0)
	add	a5,a4,a5
	lw	a4,-36(s0)
	add	a5,a4,a5
	sw	a5,-32(s0)
	lw	a5,-20(s0)
	beq	a5,zero,.L12
	lw	a5,-36(s0)
	li	a4,45
	sb	a4,0(a5)
.L12:
	lw	a5,-32(s0)
	addi	a4,a5,-1
	sw	a4,-32(s0)
	sb	zero,0(a5)
.L13:
	lw	a4,-52(s0)
	li	a5,10
	rem	a5,a4,a5
	andi	a4,a5,0xff
	lw	a5,-32(s0)
	addi	a3,a5,-1
	sw	a3,-32(s0)
	addi	a4,a4,48
	andi	a4,a4,0xff
	sb	a4,0(a5)
	lw	a4,-52(s0)
	li	a5,10
	div	a5,a4,a5
	sw	a5,-52(s0)
	lw	a5,-52(s0)
	bne	a5,zero,.L13
	lw	a5,-36(s0)
	mv	a0,a5
	lw	ra,60(sp)
	lw	s0,56(sp)
	addi	sp,sp,64
	jr	ra
	.size	toString, .-toString
	.align	2
	.globl	my_array_alloc
	.type	my_array_alloc, @function
my_array_alloc:
	addi	sp,sp,-48
	sw	ra,44(sp)
	sw	s0,40(sp)
	addi	s0,sp,48
	sw	a0,-36(s0)
	lw	a5,-36(s0)
	addi	a5,a5,1
	slli	a5,a5,2
	mv	a0,a5
	call	malloc
	mv	a5,a0
	sw	a5,-20(s0)
	lw	a5,-20(s0)
	lw	a4,-36(s0)
	sw	a4,0(a5)
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,44(sp)
	lw	s0,40(sp)
	addi	sp,sp,48
	jr	ra
	.size	my_array_alloc, .-my_array_alloc
	.align	2
	.globl	my_array_size
	.type	my_array_size, @function
my_array_size:
	addi	sp,sp,-32
	sw	s0,28(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a5,-20(s0)
	lw	a5,0(a5)
	mv	a0,a5
	lw	s0,28(sp)
	addi	sp,sp,32
	jr	ra
	.size	my_array_size, .-my_array_size
	.align	2
	.globl	my_c_string_substring
	.type	my_c_string_substring, @function
my_c_string_substring:
	addi	sp,sp,-48
	sw	ra,44(sp)
	sw	s0,40(sp)
	addi	s0,sp,48
	sw	a0,-36(s0)
	sw	a1,-40(s0)
	sw	a2,-44(s0)
	lw	a4,-44(s0)
	lw	a5,-40(s0)
	sub	a5,a4,a5
	addi	a5,a5,1
	mv	a0,a5
	call	malloc
	mv	a5,a0
	sw	a5,-20(s0)
	lw	a5,-40(s0)
	lw	a4,-36(s0)
	add	a3,a4,a5
	lw	a4,-44(s0)
	lw	a5,-40(s0)
	sub	a5,a4,a5
	mv	a2,a5
	mv	a1,a3
	lw	a0,-20(s0)
	call	memcpy
	lw	a4,-44(s0)
	lw	a5,-40(s0)
	sub	a5,a4,a5
	mv	a4,a5
	lw	a5,-20(s0)
	add	a5,a5,a4
	sb	zero,0(a5)
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,44(sp)
	lw	s0,40(sp)
	addi	sp,sp,48
	jr	ra
	.size	my_c_string_substring, .-my_c_string_substring
	.align	2
	.globl	my_c_string_parseInt
	.type	my_c_string_parseInt, @function
my_c_string_parseInt:
	addi	sp,sp,-48
	sw	ra,44(sp)
	sw	s0,40(sp)
	addi	s0,sp,48
	sw	a0,-36(s0)
	addi	a5,s0,-20
	mv	a2,a5
	lui	a5,%hi(.LC0)
	addi	a1,a5,%lo(.LC0)
	lw	a0,-36(s0)
	call	sscanf
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,44(sp)
	lw	s0,40(sp)
	addi	sp,sp,48
	jr	ra
	.size	my_c_string_parseInt, .-my_c_string_parseInt
	.align	2
	.globl	my_c_string_ord
	.type	my_c_string_ord, @function
my_c_string_ord:
	addi	sp,sp,-32
	sw	s0,28(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a5,-24(s0)
	lw	a4,-20(s0)
	add	a5,a4,a5
	lbu	a5,0(a5)
	mv	a0,a5
	lw	s0,28(sp)
	addi	sp,sp,32
	jr	ra
	.size	my_c_string_ord, .-my_c_string_ord
	.align	2
	.globl	my_c_string_length
	.type	my_c_string_length, @function
my_c_string_length:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a0,-20(s0)
	call	strlen
	mv	a5,a0
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	my_c_string_length, .-my_c_string_length
	.align	2
	.globl	my_string_plus
	.type	my_string_plus, @function
my_string_plus:
	addi	sp,sp,-48
	sw	ra,44(sp)
	sw	s0,40(sp)
	addi	s0,sp,48
	sw	a0,-36(s0)
	sw	a1,-40(s0)
	lw	a0,-36(s0)
	call	strlen
	mv	a5,a0
	sw	a5,-20(s0)
	lw	a0,-40(s0)
	call	strlen
	mv	a5,a0
	sw	a5,-24(s0)
	lw	a4,-20(s0)
	lw	a5,-24(s0)
	add	a5,a4,a5
	mv	a0,a5
	call	malloc
	mv	a5,a0
	sw	a5,-28(s0)
	lw	a5,-20(s0)
	mv	a2,a5
	lw	a1,-36(s0)
	lw	a0,-28(s0)
	call	memcpy
	lw	a5,-20(s0)
	lw	a4,-28(s0)
	add	a5,a4,a5
	lw	a4,-24(s0)
	mv	a2,a4
	lw	a1,-40(s0)
	mv	a0,a5
	call	memcpy
	lw	a4,-20(s0)
	lw	a5,-24(s0)
	add	a5,a4,a5
	mv	a4,a5
	lw	a5,-28(s0)
	add	a5,a5,a4
	sb	zero,0(a5)
	lw	a5,-28(s0)
	mv	a0,a5
	lw	ra,44(sp)
	lw	s0,40(sp)
	addi	sp,sp,48
	jr	ra
	.size	my_string_plus, .-my_string_plus
	.align	2
	.globl	my_string_eq
	.type	my_string_eq, @function
my_string_eq:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	seqz	a5,a5
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	my_string_eq, .-my_string_eq
	.align	2
	.globl	my_string_neq
	.type	my_string_neq, @function
my_string_neq:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	snez	a5,a5
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	my_string_neq, .-my_string_neq
	.align	2
	.globl	my_string_le
	.type	my_string_le, @function
my_string_le:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	srli	a5,a5,31
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	my_string_le, .-my_string_le
	.align	2
	.globl	my_string_ge
	.type	my_string_ge, @function
my_string_ge:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	sgt	a5,a5,zero
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	my_string_ge, .-my_string_ge
	.align	2
	.globl	my_string_leq
	.type	my_string_leq, @function
my_string_leq:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	slti	a5,a5,1
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	my_string_leq, .-my_string_leq
	.align	2
	.globl	my_string_geq
	.type	my_string_geq, @function
my_string_geq:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	not	a5,a5
	srli	a5,a5,31
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	my_string_geq, .-my_string_geq
	.ident	"GCC: (GNU) 10.2.0"
