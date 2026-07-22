import { useState, type ReactNode } from 'react'
import {
  Badge,
  Button,
  Card,
  ConfirmDialog,
  Dialog,
  EmptyState,
  ErrorState,
  Input,
  Select,
  Skeleton,
  Spinner,
  Table,
  Tabs,
  Textarea,
  type TableColumn,
} from '../components/ui'
import { useToast } from '../hooks/useToast'

interface SampleRow {
  id: number
  product: string
  price: string
  status: '진행 중' | '낙찰' | '대기'
}

const sampleRows: SampleRow[] = [
  { id: 1, product: '무선 기계식 키보드', price: '128,000원', status: '진행 중' },
  { id: 2, product: '빈티지 필름 카메라', price: '245,000원', status: '낙찰' },
  { id: 3, product: '스테인리스 드립 세트', price: '58,000원', status: '대기' },
]

const tableColumns: TableColumn<SampleRow>[] = [
  {
    key: 'product',
    header: '상품명',
    cell: (row) => <strong className="font-semibold text-ink">{row.product}</strong>,
  },
  { key: 'price', header: '현재 가격', cell: (row) => row.price, align: 'right' },
  {
    key: 'status',
    header: '상태',
    cell: (row) => (
      <Badge variant={row.status === '진행 중' ? 'primary' : row.status === '낙찰' ? 'success' : 'neutral'}>
        {row.status}
      </Badge>
    ),
    align: 'center',
  },
]

function Section({ title, description, children }: { title: string; description?: string; children: ReactNode }) {
  return (
    <section className="border-t border-line py-10 first:border-t-0">
      <div className="mb-6">
        <h2 className="text-xl font-semibold text-ink sm:text-2xl">{title}</h2>
        {description && <p className="mt-2 text-sm leading-6 text-ink-secondary">{description}</p>}
      </div>
      {children}
    </section>
  )
}

export function DesignSystemPage() {
  const { showToast } = useToast()
  const [tab, setTab] = useState('ongoing')
  const [description, setDescription] = useState('정성스럽게 보관한 상품입니다.')
  const [category, setCategory] = useState('')
  const [dialogOpen, setDialogOpen] = useState(false)
  const [confirmOpen, setConfirmOpen] = useState(false)
  const [dangerOpen, setDangerOpen] = useState(false)
  const [confirmLoading, setConfirmLoading] = useState(false)
  const [tableState, setTableState] = useState<'data' | 'loading' | 'empty'>('data')

  const completeConfirmation = () => {
    setConfirmLoading(true)
    window.setTimeout(() => {
      setConfirmLoading(false)
      setConfirmOpen(false)
      showToast({ message: '요청이 완료되었습니다.', variant: 'success' })
    }, 900)
  }

  return (
    <main className="min-h-screen bg-surface">
      <div className="mx-auto max-w-6xl px-5 sm:px-8 lg:px-10">
        <header className="flex flex-col gap-4 py-8 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <Badge variant="primary">Development only</Badge>
            <h1 className="mt-4 text-3xl font-bold text-ink">Design System</h1>
            <p className="mt-2 max-w-2xl text-sm leading-6 text-ink-secondary">
              경매 서비스에서 재사용할 기본 인터페이스와 상태를 한곳에서 확인합니다.
            </p>
          </div>
          <div className="flex items-center gap-2 text-xs text-ink-muted">
            <span className="h-3 w-3 rounded-full bg-brand" aria-hidden="true" />
            Primary #7353EA
          </div>
        </header>

        <Section title="Button" description="핵심 액션, 보조 액션, 위험 작업을 명확히 구분합니다.">
          <div className="flex flex-wrap items-center gap-3">
            <Button variant="primary">Primary</Button>
            <Button variant="secondary">Secondary</Button>
            <Button variant="outline">Outline</Button>
            <Button variant="danger">Danger</Button>
            <Button variant="ghost">Ghost</Button>
            <Button disabled>Disabled</Button>
            <Button isLoading>저장</Button>
          </div>
          <div className="mt-4 flex flex-wrap items-center gap-3">
            <Button size="sm" variant="outline">Small</Button>
            <Button size="md" variant="outline">Medium</Button>
            <Button size="lg">Large action</Button>
          </div>
        </Section>

        <Section title="Form controls" description="레이블, 안내 문구, 오류 상태를 필드와 연결합니다.">
          <div className="grid gap-6 md:grid-cols-2">
            <Input
              label="상품명"
              placeholder="상품명을 입력하세요"
              helperText="최대 60자까지 입력할 수 있습니다."
              required
            />
            <Input
              label="입찰 금액"
              defaultValue="125000"
              error="현재 최고가보다 높은 금액을 입력해주세요."
              suffix="원"
            />
            <Input label="검색" placeholder="키워드 입력" prefix="⌕" disabled />
            <Select
              label="카테고리"
              placeholder="카테고리를 선택하세요"
              value={category}
              onChange={(event) => setCategory(event.target.value)}
              options={[
                { value: 'digital', label: '디지털' },
                { value: 'living', label: '리빙' },
                { value: 'fashion', label: '패션' },
                { value: 'closed', label: '선택 불가 예시', disabled: true },
              ]}
              required
            />
            <Textarea
              containerClassName="md:col-span-2"
              label="상품 설명"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              maxLength={120}
              showCount
              placeholder="상품 상태와 특징을 입력하세요"
              required
            />
          </div>
        </Section>

        <Section title="Card & Badge">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
              <h3 className="text-base font-semibold text-ink">기본 카드</h3>
              <p className="mt-2 text-sm leading-6 text-ink-secondary">일반 정보를 담는 중립적인 컨테이너입니다.</p>
            </Card>
            <Card
              variant="clickable"
              onClick={() => showToast({ message: '카드를 선택했습니다.', variant: 'info' })}
            >
              <h3 className="text-base font-semibold text-ink">클릭 가능한 카드</h3>
              <p className="mt-2 text-sm leading-6 text-ink-secondary">키보드 Enter와 Space로도 선택됩니다.</p>
            </Card>
            <Card variant="highlight">
              <h3 className="text-base font-semibold text-brand-dark">강조 카드</h3>
              <p className="mt-2 text-sm leading-6 text-ink-secondary">현재 선택되거나 중요한 내용을 표시합니다.</p>
            </Card>
            <Card variant="danger">
              <h3 className="text-base font-semibold text-danger">위험 작업 카드</h3>
              <p className="mt-2 text-sm leading-6 text-ink-secondary">되돌리기 어려운 작업을 분리합니다.</p>
            </Card>
          </div>
          <div className="mt-5 flex flex-wrap gap-2">
            <Badge>Default</Badge>
            <Badge variant="primary">최고 입찰 중</Badge>
            <Badge variant="success">낙찰 완료</Badge>
            <Badge variant="warning">마감 임박</Badge>
            <Badge variant="error">승인 거절</Badge>
            <Badge variant="neutral">종료</Badge>
          </div>
        </Section>

        <Section title="Tabs" description="좌우 방향키, Home, End로 탭 사이를 이동할 수 있습니다.">
          <Tabs
            ariaLabel="상품 상태"
            value={tab}
            onChange={setTab}
            items={[
              { value: 'ongoing', label: '진행 중', panelId: 'tab-panel' },
              { value: 'ended', label: '종료', panelId: 'tab-panel' },
              { value: 'disabled', label: '비활성', disabled: true },
              { value: 'long', label: '모바일 오버플로 확인용 긴 탭', panelId: 'tab-panel' },
            ]}
          />
          <div id="tab-panel" role="tabpanel" className="min-h-24 py-6 text-sm text-ink-secondary">
            선택된 탭: <strong className="text-brand-dark">{tab}</strong>
          </div>
        </Section>

        <Section title="Table" description="모바일에서는 가로 스크롤 또는 stack 모드를 선택할 수 있습니다.">
          <div className="mb-4 flex flex-wrap gap-2">
            <Button size="sm" variant={tableState === 'data' ? 'primary' : 'secondary'} onClick={() => setTableState('data')}>데이터</Button>
            <Button size="sm" variant={tableState === 'loading' ? 'primary' : 'secondary'} onClick={() => setTableState('loading')}>로딩</Button>
            <Button size="sm" variant={tableState === 'empty' ? 'primary' : 'secondary'} onClick={() => setTableState('empty')}>빈 상태</Button>
          </div>
          <Table
            caption="경매 상품 예시"
            columns={tableColumns}
            rows={tableState === 'data' ? sampleRows : []}
            getRowKey={(row) => row.id}
            isLoading={tableState === 'loading'}
            emptyTitle="등록된 상품이 없습니다."
            emptyDescription="새 상품이 등록되면 이곳에 표시됩니다."
            mobileMode="stack"
          />
        </Section>

        <Section title="Dialog & Toast">
          <div className="flex flex-wrap gap-3">
            <Button variant="outline" onClick={() => setDialogOpen(true)}>기본 Dialog</Button>
            <Button onClick={() => setConfirmOpen(true)}>Confirm Dialog</Button>
            <Button variant="danger" onClick={() => setDangerOpen(true)}>Danger Confirm</Button>
          </div>
          <div className="mt-4 flex flex-wrap gap-2">
            {(['success', 'error', 'warning', 'info'] as const).map((variant) => (
              <Button
                key={variant}
                size="sm"
                variant="secondary"
                onClick={() => showToast({ message: `${variant} 알림 예시입니다.`, variant })}
              >
                {variant} Toast
              </Button>
            ))}
          </div>
        </Section>

        <Section title="Loading & Skeleton">
          <div className="flex items-end gap-6">
            <Spinner size="sm" label="작은 로딩" />
            <Spinner size="md" label="보통 로딩" />
            <Spinner size="lg" label="큰 로딩" />
          </div>
          <div className="mt-6 grid gap-5 lg:grid-cols-2">
            <Skeleton variant="product-card" />
            <div className="grid gap-5">
              <Card><Skeleton variant="balance" /></Card>
              <Card><Skeleton variant="list" lines={2} /></Card>
            </div>
          </div>
          <div className="mt-5"><Skeleton variant="detail" /></div>
        </Section>

        <Section title="Empty & Error state">
          <div className="grid gap-4 md:grid-cols-2">
            <div className="rounded-card border border-line">
              <EmptyState
                title="아직 등록된 항목이 없습니다."
                description="새로운 항목이 생기면 이곳에서 확인할 수 있습니다."
                icon={<span>○</span>}
                action={<Button variant="secondary">선택적 액션</Button>}
              />
            </div>
            <div className="rounded-card border border-line">
              <ErrorState
                description="정보를 불러오지 못했습니다. 잠시 후 다시 시도해주세요."
                onRetry={() => showToast({ message: '다시 시도했습니다.', variant: 'info' })}
              />
            </div>
          </div>
        </Section>
      </div>

      <Dialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title="기본 대화상자"
        description="간단한 설명이나 추가 내용을 제공할 때 사용합니다. ESC 또는 배경을 눌러 닫을 수 있습니다."
        footer={<Button onClick={() => setDialogOpen(false)}>확인</Button>}
      />
      <ConfirmDialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onConfirm={completeConfirmation}
        title="충전을 요청할까요?"
        description="입력한 금액으로 충전 승인 요청을 등록합니다."
        confirmLabel="요청하기"
        isLoading={confirmLoading}
      />
      <ConfirmDialog
        open={dangerOpen}
        onClose={() => setDangerOpen(false)}
        onConfirm={() => setDangerOpen(false)}
        title="이 작업을 진행할까요?"
        description="이 작업은 되돌리기 어려울 수 있습니다. 내용을 다시 확인해주세요."
        confirmLabel="계속하기"
        danger
      />
    </main>
  )
}
